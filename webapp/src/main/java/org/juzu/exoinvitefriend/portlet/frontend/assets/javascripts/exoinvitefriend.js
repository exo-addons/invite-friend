/**
 * Created by exoplatform on 27/01/15.
 */
(function($){

  var _messageConfirmCBController = function (type,message) {
    var alertDOM =  $('#JuzExoInviteFriendAlertContainer');
    if(type != null && type != "") {
      var icon = type.charAt(0).toUpperCase() + type.slice(1);
      var strIcon = "<i class='uiIcon" + icon + "'></i>";
      alertDOM.removeClass();
      alertDOM.addClass('alert');
      alertDOM.addClass('alert-' + type);
      alertDOM.html(strIcon + message);
      alertDOM.css('visibility', 'visible');
      setTimeout(function() {
        alertDOM.css("visibility" , "hidden");
      }, 5000);
    }
  };

  var _disPlayInfoMsgCB = function(msg){
    _messageConfirmCBController('info',msg);
  };
  var _disPlaySuccessMsgCB = function(msg){
    _messageConfirmCBController('success',msg);
  };
  var _disPlayWarningMsgCB = function(msg){
    _messageConfirmCBController('warning',msg);
  };
  var _disPlayErrorMsgCB = function(msg){
    _messageConfirmCBController('error',msg);
  };

  function _validateEmail(email){
    var regex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}/i;
    if (email.length === 0 || !regex.test(email)) {
      _disPlayErrorMsgCB("Please enter a valid email address.");
      return false;
    }
    return true;
  };
  function _sendInvitation(email){
    _disPlayInfoMsgCB('sending ... ');
    $('.jz').jzAjax('JuZExoInviteFriendFrontendApplication.sendInvitation()',{
      data:{email:email},
      success:function(data){
        if(data == 'ok'){
          _disPlaySuccessMsgCB("sent successfully");
          _storeInvitation(email);
        }else if(data == 'nok'){
          _disPlayErrorMsgCB("Something went wrong, cannot remove your account");
        }else{
          _disPlayInfoMsgCB(data);
        }
      }
    });
  };
  function _storeInvitation(email){
     $('.jz').jzAjax('JuZExoInviteFriendFrontendApplication.storeInvitation()',{
      data:{email:email},
      success:function(data){
        if(data == 'ok'){
//          _disPlaySuccessMsgCB(data);
        }else if(data == 'nok'){
//          _disPlayErrorMsgCB("Something went wrong, cannot remove your account");
        }else{
//          _disPlayInfoMsgCB(data);
        }
      }
    });
  };

  function _saveEditMode(enableStoreData){
    $('.jz').jzAjax('JuZExoInviteFriendFrontendApplication.saveEditMode()',{
      data:{enableStoreData:enableStoreData},
      success:function(data){
        if(data == 'ok')
          alert('settings saved');
      }
    });
  }
  $(document).ready(function(){
    $(document).on('click.exo-invite-friend-send','button#exo-invite-friend-send',function(){
      var email = $("#exo-invite-friend-invitee").val();
      if(_validateEmail(email))
        _sendInvitation(email);
    });
    /*
    $(document).on('click.exo-invite-friend-settings-save','button#exo-invite-friend-edit-save',function(){
      var enableStoreData = "false";
      if($("#exo-invite-friend-enableStoreData").prop("checked"))
        enableStoreData = "true";
      _saveEditMode(enableStoreData);
    });
    */
  });

})($);