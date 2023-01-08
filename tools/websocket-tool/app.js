var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#messages").show();
        $("#messages").html("");
    }
}

function connect() {
    var socket = new SockJS($("#websocketURL").val());
    stompClient = Stomp.over(socket);
    var headers = {};
    if ($("#websocketToken").val() != null) {
        headers["Authorization"] = "Bearer " + $("#websocketToken").val();
    }
    stompClient.connect(headers,
        function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        var headers = {};
        if ($("#websocketToken").val() != null) {
            headers["Authorization"] = "Bearer " + $("#websocketToken").val();
        }
        stompClient.subscribe($("#websocketTopic").val(), function (message) {
            console.log(message)
            showMessage("[ in ] " + message.body);
        }, headers);
    }, (err) => {
        console.log(err)
        showMessage("[ err ] " + err)
        setConnected(false);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    var headers = {};
    if ($("#websocketToken").val() != null) {
        headers["Authorization"] = "Bearer " + $("#websocketToken").val();
    }
    stompClient.send($("#websocketTopic").val(), headers, JSON.stringify($("#message").val()));
    showMessage("[ out ] " + $("#message").val());
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); });
});