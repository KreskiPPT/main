var MenagerGraczy = function() {
    this.gracze = [];
    this.WyborKoloru = new WyborKoloru(Config.colorSaturation, Config.colorValue);

};


MenagerGraczy.prototype.addPlayer = function(name) {
    var newPlayer = new Gracze();
    
    newPlayer.name = name;
    newPlayer.color = this.getColor();
    newPlayer.ID = this.gracze.length;

    return this.playerPush(newPlayer);
};

var Utilities = {
    random: function(minimum, maximum) {
        return Math.floor(Math.random() * (maximum - minimum + 1)) + minimum;
    }
};

MenagerGraczy.prototype.playerPush = function (newPlayer) {
	for (var i=0; i < this.gracze.length; i++) {
		var player = this.gracze[i];
		
		if (player.canceled) {
			this.gracze[i] = newPlayer;
			return i;
		}
	}
	
	this.gracze.push(newPlayer);
	return this.gracze.length - 1;
};



MenagerGraczy.prototype.initializePlayers = function() {
    for (var i = 0; i < this.gracze.length; i++) {
        var player = this.gracze[i];

        player.x = Utilities.random(Config.canvasWidth / 4, 3 * Config.canvasWidth / 4);;
        player.y = Utilities.random(Config.canvasHeight / 4, 3 * Config.canvasHeight / 4);;
        player.angle = Math.random() * 360;		
        player.isPlaying = true;
        player.isAlive = true;
		player.resetTimeout();
    }
};




MenagerGraczy.prototype.numberOfPlayers = function() {
    var count = 0;

    for (var i = 0; i < this.gracze.length; i++) {
        if (!this.gracze[i].canceled) {
            count++;
        }
    }

    return count;
};

MenagerGraczy.prototype.getColor = function() {
    return this.WyborKoloru.convertRGBToHex(this.WyborKoloru.getColor());
};

