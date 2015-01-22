var Silnik = function(context, players) {
    this.intervalID = 0;
    this.drawingContext = context;
    this.gracze = players;
	this.onCollision = null;
	this.onRoundOver = null;
	this.lastHit = null;
	this.countWins = false;
	this.playerRank;
};



Silnik.prototype.start = function() {
    var that = this;
	this.playerRank = [];
    if (this.intervalID === 0) {
        this.intervalID = setInterval(function() {
            that.draw();
        }, 15);    
    }
};

Silnik.prototype.stop = function() {
    clearInterval(this.intervalID);
    this.intervalID = 0; 
};

Silnik.prototype.draw = function() {
    var player,
        deltaX,
        deltaY,
		hit = false;
		
    for (var i = 0; i < this.gracze.length; i++) {
        player = this.gracze[i];

		if (!player.isPlaying || !player.isAlive || player.canceled) {
			continue;
		}
		
		var speed = Config.pixelsPerSecond * (1000 / Config.frameRate / 1000);


        if(player.left==true){
        	player.angle+=0.0005;
        	deltaX=Math.cos(player.angle*(180/Math.PI))*50+player.px;
        	deltaY=Math.sin(player.angle*(180/Math.PI))*50+player.py;
        	
        }
        else if(player.right==true){
        	player.angle-=0.0005;
        	deltaX=-Math.cos(player.angle*(180/Math.PI))*50+player.px;
        	deltaY=-Math.sin(player.angle*(180/Math.PI))*50+player.py;
        	
        }
        else{
        	deltaX = player.x-Math.sin(player.angle*(180/Math.PI));
       		deltaY = player.y+Math.cos(player.angle*(180/Math.PI));
        }
		
		if (player.hole === 0) { 
			if (this.hitTest({x: deltaX, y: deltaY})) {
				
				this.playerRank.unshift(player.ID);
				
				player.isAlive = false;
				hit = true;

				var count = 0;
				for (var j = 0; j < this.gracze.length; j++) {
					if (this.gracze[j].isAlive && this.gracze[j].isPlaying && !this.gracze[j].canceled) {
						
						count++;
						
						if (this.countWins) {
							this.gracze[j].wins++;
						}
					}
				}
				
				if (count < 2) this.stop();
					
				this.checkForCallback(player.ID);
				
				if (count < 2) {
					if (this.onRoundOver) {
						this.onRoundOver();
					}

					return;
				}
			} 
		

			this.drawingContext.strokeStyle = player.color;
			this.drawingContext.fillStyle = player.color;
			this.drawingContext.beginPath();
			this.drawingContext.lineWidth = Config.lineWidth;
			this.drawingContext.moveTo(player.x, player.y);
			this.drawingContext.lineTo(deltaX,deltaY);
			this.drawingContext.stroke();
			
		} else {
			
			player.hole--;	
			
			if (player.hole === 0) {
				player.calculateNextHole();
			}
		}
		
        player.x = deltaX;
        player.y = deltaY;
		player.distance += Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    } 
	
	if (!hit) {
        this.lastHit = null;
    }
};

Silnik.prototype.hitTest = function(point) {

    if (point.x > Config.canvasWidth || point.y > Config.canvasHeight || point.x < 0 || point.y < 0) {
        return true;
    }	
    	

	if (this.drawingContext.getImageData(point.x, point.y, 1,1).data[3] >100) {
		return true;
	}
	
	
	return false;
};

Silnik.prototype.checkForCallback = function(ID) {
	
	//Jeszcze nie działa	
	if (!this.onCollision) {
        return;
    }
	if (this.lastHit === null || this.lastHit != ID) {
        this.onCollision(ID);
    }
	this.lastHit = ID;
};

/* ---- Getter & Setter ---- */
Silnik.prototype.setCollisionCallback = function(callback) {
	this.onCollision = callback;
};

Silnik.prototype.setRoundCallback = function(callback) {
	this.onRoundOver = callback;
};


