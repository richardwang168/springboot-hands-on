$(document).ready(function() {
    $("#logoutLink").on("click", function(e) {
        e.preventDefault();
        document.logoutForm.submit();
    });
});

$(document).ready(function(){
  $("#formTo").submit(function(event){
    event.preventDefault();
    var name = $("input[name='name']",this).val();
    var email = $("input[name='email']",this).val();
  });
});