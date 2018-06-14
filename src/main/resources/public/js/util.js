function sendItem(){
  function getXmlHttp(){
    var xmlhttp
    try{xmlhttp= new ActiveXObject("Msxml2.XMLHTTP");
    } catch(e){
      try{xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");} catch (e){xmlhttp = false}
    }
    if (!xmlhttp && typeof XMLHttpRequest!='undefined') xmlhttp = new XMLHttpRequest()
    return xmlhttp
  }
  var xhr = getXmlHttp()
  var quantity= document.getElementById("quantity").value
  var pathname = window.location.pathname
  var id = pathname.replace("/id/","")
  var request = "/addToBasket/"+pathname.replace("/id/","")+"/"+quantity
  xhr.open("GET", request, true)
  xhr.onreadystatechange=function(){
    if (xhr.readyState != 4) return clearTimeout(xhrTimeout)
  //  if (xhr.status == 200)  alert("response"+xhr.responseText)
  }
  xhr.send("a=5&b=4")
  var xhrTimeout = setTimeout( function(){ xhr.abort(); handleError("Timeout") }, 10000);
  function handleError(message) {
              alert("Ошибка: "+message)
  }
}
