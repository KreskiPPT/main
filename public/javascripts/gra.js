var Gra = function(canvasID, canvasWidth, canvasHeight) {

	Config.canvasWidth = canvasWidth;
    Config.canvasHeight = canvasHeight;
    this.canvasElement = document.getElementById(canvasID);

	this.canvasElement.width = Config.canvasWidth;
    this.canvasElement.height = Config.canvasHeight; 
    
    if (this.canvasElement.getContext) 
	{
        this.drawingContext = this.canvasElement.getContext('2d');
    } 
	else 
	{
        throw 'Use different browser, no canvas support';
    }
    this.menagerGraczy = new MenagerGraczy();
	this.silnik = new Silnik(this.drawingContext, this.menagerGraczy.gracze);
	this.engineOnHalt = false;
};

Gra.prototype.getDrawingContext = function() {
    return this.drawingContext;  
};

Gra.prototype.start = function() {

	if (this.menagerGraczy.numberOfPlayers() < 2) {
		this.engineOnHalt = true;
		this.drawFrame();
		return;
	}
	this.drawFrame();
	this.menagerGraczy.initializePlayers();
	this.silnik.start();
	this.engineOnHalt = false;
	
};

Gra.prototype.restart = function() {
	this.drawingContext.clearRect(0, 0, Config.canvasWidth, Config.canvasHeight);
	this.start();
};


Gra.prototype.addPlayer = function(name) {
    var playerID = this.menagerGraczy.addPlayer(name);
	
	if (this.engineOnHalt) {
		this.start();
	}
	return playerID;
};


Gra.prototype.drawFrame = function () {
	this.drawingContext.lineWidth = 10;
	this.drawingContext.strokeStyle = "#E3D42E";
	this.drawingContext.strokeRect(0, 0, Config.canvasWidth - 0, Config.canvasHeight - 0);
};