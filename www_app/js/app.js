var sendTarget = "all";

$(function () {
	$("#btn_send_all_players_to").on("click", function () {
		sendTarget = "all";
		$('#select_server_modal').modal('show');
	});

	$("#btn_select_server_send").on("click", function () {
		let serverName = $('#select_server option:selected').val();

		console.log("Target server: " + serverName);

		if (sendTarget == "all") {
			$.getJSON("/api/send_players?server=" + encodeURIComponent(serverName), function (data) {
				console.log(data);
				if (data.success) {
					$('#select_server_modal').modal('hide');
					showMessage("Success");
				} else {
					showError(data.message);
				}
			});
		} else {
			$.getJSON("/api/send_player?server=" + encodeURIComponent(serverName) + "&player=" + encodeURIComponent(sendTarget), function (data) {
				console.log(data);
				if (data.success) {
					$('#select_server_modal').modal('hide');
					showMessage("Success");
				} else {
					showError(data.message);
				}
			});
		}
	});

	$("#link_team_editor").on("click", function () {
		window.open("/app/team_editor/");
	});

	$("#link_start_game").on("click", function () {
		$('#start_game_modal').modal('show');
	});

	$("#link_reset_data").on("click", function () {
		$('#reset_modal').modal('show');
	});

	$("#link_rename_tournament").on("click", function () {
		$('#rename_tournament_modal').modal('show');
	});


	$("#link_broadcast_message").on("click", function () {
		$('#broadcast_modal').modal('show');
	});

	$("#btn_broadcast").on("click", function () {
		runBroadcast();
	})

	$("#btn_reset").on("click", function () {
		if (confirm("You are about to reset the score and game data!!!")) {
			$.getJSON("/api/reset", function (data) {
				if (data.success) {
					showMessage("Game and score data cleared");
				} else {
					showError(data.message);
				}
			});
		}

		$('#reset_modal').modal('hide');
	})

	$("#link_clear_player_data").on("click", function () {
		$("#remove_player_data_modal").modal("show");
	});

	$("#btn_remove_player_data").on("click", function () {
		if (confirm("You are about to delete all player data! This will remove all players and their score from the database. After this players might need to reconnect")) {
			$.getJSON("/api/clear_players", function (data) {
				if (data.success) {
					showInfo("Player data cleared");
					$("#remove_player_data_modal").modal("hide");
				} else {
					showError(data.message);
				}
			});
		}
	});

	

	$("#btn_start_game").on("click", function () {
		$.getJSON("/api/start_game", function (data) {
			//console.log(data);
			if (data.success) {
				$('#start_game_modal').modal('hide');
				showMessage("Game start request sent");
			} else {
				showError(data.message);
			}
		});
	});

	$.getJSON("/api/status", function (data) {
		console.log(data);
		for (let i = 0; i < data.servers.length; i++) {
			let server = data.servers[i];
			//console.log(server);

			/*$("#select_server").append(
				$("<option></option>").text(server.name).attr('value', server.name)
			)*/
			$("#select_server").append(new Option(server.name, server.name));
		}

		$("#tournament_name_input").val(data.tournament_name);

		$("#tournament_name").text(data.tournament_name);
		$("#proxy_software").text(data.proxy_software);
		$("#proxy_software_version").text(data.proxy_software_version);
		$("#available_processors").text(data.available_processors);
	});

	$("#btn_rename_tournament").on("click", function() {
		let newName = $("#tournament_name_input").val();
		if(confirm("You are about to rename the tournament to " + newName + ". This will require a restart to apply all changes")) {
			$.getJSON("/api/set_tournament_name?name=" + encodeURI(newName), function(data) {
				if(data.success) {
					showInfo("Tournament renamed. Please restart the servers to apply");
				} else {
					showError(data.message);
				}
			});
		}
	});

	setInterval(function () {
		update();
	}, 1000);

	update();
});

function update() {
	$.getJSON("/api/status", function (data) {
		let uuidList = [];

		$("#tournament_name").text(data.tournament_name);
		$("#authorized_player_count").text(data.authorized_players);
		$("#online_player_count").text(data.players.length);

		for (let i = 0; i < data.players.length; i++) {
			let player = data.players[i];
			//console.log(player);

			let uuid = player.uuid;

			uuidList.push(uuid);

			let playerExists = $('.player-tr[data-uuid="' + uuid + '"]').length > 0;

			let playerObject = null;

			if (playerExists) {
				playerObject = $('.player-tr[data-uuid="' + uuid + '"]')
			} else {
				let newPlayer = $("#player_template").clone();
				newPlayer.removeAttr('id');
				newPlayer.addClass("player-tr-real");
				newPlayer.attr("data-uuid", uuid);

				$("#players_tbody").append(newPlayer);

				playerObject = newPlayer;

				newPlayer.find(".btn-send-player-to").on("click", function () {
					let uuid = $(this).parent().parent().data("uuid");

					sendTarget = uuid;

					console.log("UUID is: " + uuid);
					$('#select_server_modal').modal('show');
				});
			}

			playerObject.find(".player-server").text(player.server);
			playerObject.find(".player-username").text(player.name);
			playerObject.find(".player-uuid").text(player.uuid);

			playerObject.find(".player-score").text(player.score);
			playerObject.find(".player-kills").text(player.kills);
			playerObject.find(".player-team-score").text(player.team_score);

			let team = player.team_number == -1 ? "None" : "Team " + player.team_number;

			playerObject.find(".player-team").text(team);
		}

		//console.log(uuidList);

		$(".player-tr-real").each(function () {
			let uuid = $(this).data("uuid");

			if (!uuidList.includes(uuid)) {
				$(this).remove();
			}
		});
	});
}

function setCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires=" + d.toUTCString();
	document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length, c.length);
		}
	}
	return "";
}

function showInfo(message) {
	showMessageWithIcon(message, "info", "./img/outline-info-24px.svg");
}

function showWarning(message) {
	showMessageWithIcon(message, "warning", "./img/outline-warning-24px.svg");
}

function showError(message) {
	console.warn(message);
	showMessageWithIcon(message, "danger", "./img/outline-error_outline-24px.svg");
}

function showMessage(message, type) {
	$.notify({
		message: message
	}, {
		type: type,
		allow_dismiss: true,
		delay: 3000,
		z_index: 1337
	});
}

function showMessageWithIcon(message, type, icon) {
	$.notify({
		icon: icon,
		message: "&nbsp;" + message
	}, {
		type: type,
		icon_type: "img",
		allow_dismiss: true,
		delay: 3000,
		z_index: 1337
	});
}

function runBroadcast() {
	let text = $("#broadcast_text_message").val();

	$.getJSON("/api/broadcast?message=" + encodeURIComponent(text), function (data) {
		if (data.success) {
			showMessage("Message sent");
			$("#broadcast_text_message").val("");
		} else {
			showError(data.message);
		}
	});

	$('#broadcast_modal').modal('hide');
}