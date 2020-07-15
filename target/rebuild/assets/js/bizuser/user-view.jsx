/*
Copyright (c) REBUILD <https://getrebuild.com/> and its owners. All rights reserved.

rebuild is dual-licensed under commercial and open source licenses (GPLv3).
See LICENSE and COMMERCIAL in the project root for license information.
*/

const user_id = window.__PageConfig.recordId

$(document).ready(function () {
  $('.J_delete').off('click').click(function () {
    $.get(`/admin/bizuser/delete-checks?id=${user_id}`, function (res) {
      if (res.data.hasMember === 0) {
        RbAlert.create('此用户可以被安全的删除', '删除用户', {
          icon: 'alert-circle-o',
          type: 'danger',
          confirmText: '删除',
          confirm: function () { deleteUser(user_id, this) }
        })
      } else {
        RbAlert.create('此用户已被使用过，因此不能删除。如不再使用可将其停用', '无法删除', {
          icon: 'alert-circle-o',
          type: 'danger',
          confirmText: '停用',
          confirm: function () { toggleDisabled(true, this) }
        })
      }
    })
  })

  $('.J_disable').click(() => {
    RbAlert.create('确定要停用此用户吗？', {
      confirmText: '停用',
      confirm: function () { toggleDisabled(true, this) }
    })
  })
  $('.J_enable').click(() => toggleDisabled(false))

  $('.J_changeRole').click(() => { renderRbcomp(<DlgEnableUser user={user_id} role={true} />) })
  $('.J_changeDept').click(() => { renderRbcomp(<DlgEnableUser user={user_id} dept={true} />) })

  $('.J_resetpwd').click(() => {
    const chars = 'ABCDEFGHJKMNPQRSTWXYabcdefhkmnprstwxy2345678'
    let newpwd = ''
    for (let i = 0; i < 8; i++) newpwd += chars.charAt(Math.floor(Math.random() * chars.length))
    newpwd += '!8'

    RbAlert.create(`密码将重置为 <code class="fs-13">${newpwd}</code> 是否确认？`, {
      html: true,
      confirm: function () {
        this.disabled(true)
        $.post(`/admin/bizuser/user-resetpwd?id=${user_id}&newp=${$decode(newpwd)}`, (res) => {
          this.disabled()
          if (res.error_code === 0) {
            RbHighbar.success('密码重置成功')
            this.hide()
          } else RbHighbar.error(res.error_code)
        })
      }
    })
  })

  if (rb.isAdminVerified) {
    $.get(`/admin/bizuser/check-user-status?id=${user_id}`, (res) => {
      if (res.error_code > 0) return
      if (res.data.system === true && rb.isAdminVerified === true) {
        $('.J_tips').removeClass('hide').find('.message p').text('系统内建超级管理员，不允许修改。此用户拥有最高级系统权限，请谨慎使用')
        $('.view-action').remove()
        return
      }

      const _data = res.data
      if (_data.disabled === true) {
        $('.J_disable').remove()
        if (!_data.role || !_data.dept) {
          $('.J_enable').off('click').click(() => {
            renderRbcomp(<DlgEnableUser enable={true} user={user_id} dept={!_data.dept} role={!_data.role} />)
          })
        }
      } else $('.J_enable').remove()

      if (_data.active === true) return
      let reason = []
      if (!_data.role) reason.push('未指定角色')
      else if (_data.roleDisabled) reason.push('所属角色已停用')
      if (!_data.dept) reason.push('未指定部门')
      else if (_data.deptDisabled) reason.push('所在部门已停用')
      if (_data.disabled === true) reason.push('已停用')
      $('.J_tips').removeClass('hide').find('.message p').text('当前用户处于未激活状态，因为其 ' + reason.join(' / '))
    })
  }
})

// 启用/禁用
const toggleDisabled = function (disabled, alert) {
  alert && alert.disabled(true)
  const _data = { user: user_id, enable: !disabled }
  $.post('/admin/bizuser/enable-user', JSON.stringify(_data), (res) => {
    if (res.error_code === 0) {
      RbHighbar.success('用户已' + (disabled ? '停用' : '启用'))
      setTimeout(() => location.reload(), 500)
    } else RbHighbar.error(res.error_msg)
  })
}

// 删除用户
const deleteUser = function (id, alert) {
  alert && alert.disabled(true)
  $.post(`/admin/bizuser/user-delete?id=${id}`, (res) => {
    if (res.error_code === 0) {
      parent.location.hash = '!/View/'
      parent.location.reload()
    } else RbHighbar.error(res.error_msg)
  })
}

// 激活用户/变更部门/角色
class DlgEnableUser extends RbModalHandler {

  constructor(props) {
    super(props)

    this.__title = '用户激活'
    if (!props.enable) this.__title = '变更' + (props.dept === true ? '部门' : '角色')
  }

  render() {
    return <RbModal title={this.__title} ref={(c) => this._dlg = c} disposeOnHide={true}>
      <div className="form">
        {this.props.dept === true &&
          <div className="form-group row">
            <label className="col-sm-3 col-form-label text-sm-right">用户部门</label>
            <div className="col-sm-7">
              <select className="form-control form-control-sm" ref={(c) => this._deptNew = c} />
            </div>
          </div>}
        {this.props.role === true &&
          <div className="form-group row">
            <label className="col-sm-3 col-form-label text-sm-right">用户角色</label>
            <div className="col-sm-7">
              <select className="form-control form-control-sm" ref={(c) => this._roleNew = c} />
            </div>
          </div>}
        <div className="form-group row footer">
          <div className="col-sm-7 offset-sm-3" ref={(c) => this._btns = c}>
            <button className="btn btn-primary btn-space" type="button" data-loading-text="请稍后" onClick={() => this.post()}>确定</button>
            <a className="btn btn-link btn-space" onClick={() => this.hide()}>取消</a>
          </div>
        </div>
      </div>
    </RbModal>
  }

  componentDidMount() {
    if (this._deptNew) this.__s2dept = this.__initSelect2(this._deptNew, ['Department', '部门'])
    if (this._roleNew) this.__s2role = this.__initSelect2(this._roleNew, ['Role', '角色'])
  }

  __initSelect2(el, type) {
    return $(el).select2({
      placeholder: '选择' + type[1],
      minimumInputLength: 1,
      ajax: {
        url: '/commons/search/search',
        delay: 300,
        data: function (params) {
          return { entity: type[0], q: params.term }
        },
        processResults: function (data) {
          const rs = data.data.map((item) => { return item })
          return { results: rs }
        }
      }
    })
  }

  post() {
    let data = { user: this.props.user }
    if (this.props.enable === true) data.enable = true
    if (this.__s2dept) {
      const v = this.__s2dept.val()
      if (!v) { RbHighbar.create('请选择部门'); return }
      data.dept = v
    }
    if (this.__s2role) {
      const v = this.__s2role.val()
      if (!v) { RbHighbar.create('请选择角色'); return }
      data.role = v
    }

    const btns = $(this._btns).find('.btn').button('loading')
    $.post('/admin/bizuser/enable-user', JSON.stringify(data), (res) => {
      if (res.error_code === 0) {
        if (data.enable === true) {
          RbHighbar.success('用户已激活')
          setTimeout(() => location.reload(), 500)
        } else location.reload()
      } else RbHighbar.error(res.error_msg)
      btns.button('reset')
    })
  }
}