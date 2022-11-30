var rootURL = "http://localhost:8080/sse";

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
function addAlerta(compromisso, callback) {
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL,
		dataType : "json",
		data : compromisso,
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

function deleteCompromisso(id, callback) {
	$.ajax({
		type : 'DELETE',
		url : rootURL + '?compromissoId=' + id,
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