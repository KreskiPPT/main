var Gracze = function() {
    this.x = 200;
    this.y = 200;
    this.px = 200;
    this.py = 200;
    this.left=false;
    this.right=false;
    this.speed = 1;
    this.angle = 0;
    this.name = '';
    this.color = '';
    this.ID = null;
	this.distance = 0;
	this.isPlaying = false;
	this.isAlive = false;
	this.canceled = false;
	this.hole = 0;
	this.holeTimeoutID;
	this.wins = 0;
	
	this.calculateNextHole();
};

Gracze.prototype.navigate = function(direction) {
    var maximumChangeOfAngle = 2;
	
	direction = Math.min(Math.max(direction, -1), 1);

    this.angle = this.angle + maximumChangeOfAngle * direction;

    this.angle %= 360;

    if (this.angle < 0) {
        this.angle += 360;
    }
};

Gracze.prototype.resetTimeout = function() {
	clearTimeout(this.holeTimeoutID);
}

Gracze.prototype.calculateNextHole = function() {
	var that = this;
	var time = 5 + Math.random() * 5;
	
	this.holeTimoutID = setTimeout(function() {
		that.hole = parseInt(Config.holeSize / ((1000 / Config.frameRate / 1000) * Config.pixelsPerSecond));	
	}, time * 300);	
};