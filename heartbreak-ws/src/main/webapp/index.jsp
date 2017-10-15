<html>
<body>
<h2>Hello See heartBreak</h2>

<script>
	var socket = new WebSocket("ws://" + location.host + '/websocket/count');
	socket.onmessage = function(e) {
		var msg = e.data;
		var socket = e.target;
		if(msg === 'hi') {
			socket.send('yes');
		}
	}
</script>
</body>
</html>
