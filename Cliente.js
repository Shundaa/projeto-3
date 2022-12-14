var rootURL = "http://localhost:8080/sse";
var localUser=null;
function findAll(callback) {
	$.ajax({
		type : 'GET',
		url : rootURL+'/eventos',
		dataType : "json", 
		success : callback
	});
}


function findByDate(date, callback) {

	if (date != null && date != "") {
		$.ajax({
			type : 'GET',
			url : rootURL +'/eventos'+ '?data=' + date,
			dataType : "json",
			success : callback
		});
	} else {
		findAll(callback);
	}
}

function findById(id, callback) {
	$.ajax({
		type : 'GET',
		url : rootURL + '/' + id,
		dataType : "json",
		success : callback
	});
}
function findByUser(user, callback) {
	$.ajax({
		type : 'GET',
		url : rootURL + '/' + user,
		dataType : "json",
		success : callback,
		error : function(jqXHR, textStatus, errorThrown) {
			alert('Mensagem nao recebida: ' + jqXHR.responseText);
		}
	});
}

function addUser(user, callback) {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL + '/user',
		dataType : "json",
		data : user,
		success : callback,
	});
	teste(user);
}

function addCompromisso(compromisso, callback) {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL+'/evento',
		dataType : "json",
		data : compromisso,
		success : callback,
		error : function(jqXHR, textStatus, errorThrown) {
			alert('Erro criando Compromisso: ' + jqXHR.responseText);
		}
	});
}

function updateCompromisso(id, compromisso, callback) {
	$.ajax({
		type : 'PUT',
		contentType : 'application/json',
		url : rootURL + '/' + id,
		data : compromisso,
		success : callback,
		error : function(jqXHR, textStatus, errorThrown) {
			alert('Erro atualizando contato: ' + textStatus);
		}
	});
}	
function addAlerta(compromisso, convidado, dataAlerta, callback) {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL+'/alerta?nomeEvento='+compromisso+'&nome='+convidado+'&data='+dataAlerta,
		success : callback,
		error : function(jqXHR, textStatus, errorThrown) {
			alert('Erro criando contato: ' + jqXHR.responseText);
		}
	});
}

function updateAlerta(user, compromisso, callback) {
	$.ajax({
		type : 'PUT',
		contentType : 'application/json',
		url : rootURL + '/' + user,
		data : compromisso,
		success : callback,
		error : function(jqXHR, textStatus, errorThrown) {
			alert('Erro atualizando Alerta: ' + textStatus);
		}
	});
}

function deleteCompromisso(nome, compromisso, callback) {
	$.ajax({
		type : 'DELETE',
		url : rootURL+'/evento' + '?nomeEvento=' + compromisso +'&nome=' +nome,
		success : callback,
		error : function(jqXHR, textStatus, errorThrown) {
			alert('Erro excluindo Compromisso: ' + textStatus);
		}
	});
}
function getAlertas(user,callback) { 
	$.ajax({
		type : 'GET',
		url : rootURL +'/user?nameId='+ user,
		dataType : "json",
		success : callback,
		error : function(jqXHR, textStatus, errorThrown) {
			alert('Erro recebendo alerta: ' + textStatus);
		}
});
}

function teste(user) {
	var obj = JSON.parse(user)
	console.log(obj.nome);
	if(typeof(EventSource) !== "undefined") {
	  var source = new EventSource("http://localhost:8080/sse/user?nome="+obj.nome);
	  source.onmessage = function(event) {
		document.getElementById("Eventos").innerHTML += event.data + "<br>";
	  };
	} else {
	  document.getElementById("Eventos").innerHTML = "Sorry, your browser does not support server-sent events...";
	}
	if(typeof(EventSource) !== "undefined") {
	var source = new EventSource("http://localhost:8080/sse/user?nome="+obj.nome+"_ALERTA");
		source.onmessage = function(event) {
		document.getElementById("Alerta").innerHTML += event.data + "<br>";
	};
	} else {
	  document.getElementById("Alerta").innerHTML = "Sorry, your browser does not support server-sent events...";
	}
	
}


  
