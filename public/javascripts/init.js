/* Player A: w,s Keys
 * Player B: Á, S
 */
// ---- Game Init ---- //
// Creates the Game Canvas (canvasID, width, height, fullscreen)
var gra = new Gra('gameCanvas', 600, 400, false);


// ---- Game Controlling Methods ----//
function onAddPlayer(name) {
	return gra.addPlayer(name);
}

function onRestartGame() {
	gra.restart();
}

window.addEventListener('keypress', doKeyDown, true);
window.addEventListener('keyup', doKeyUp, true);

function doKeyDown(e){
		temp_1 = gra.silnik.gracze[0];
		if(e.keyCode==119){
			temp_1.px=temp_1.x-(50*Math.cos(temp_1.angle*(180/Math.PI)));
		    temp_1.py=temp_1.y-(50*Math.sin(temp_1.angle*(180/Math.PI)));
	 		temp_1.left=true;
	 		temp_1.right=false;
	 		// this spams server with dozens of messages when player is
	 		// holding a key
			pointMessage(temp_1.x, temp_1.y, "l");
	 	}
	 	else if(e.keyCode==115){
	 		temp_1.px=temp_1.x+(50*Math.cos(temp_1.angle*(180/Math.PI)));
		   	temp_1.py=temp_1.y+(50*Math.sin(temp_1.angle*(180/Math.PI)));
	 		temp_1.right=true;
	 		temp_1.left=false;
	 		// as above - messages spam
			pointMessage(temp_1.x, temp_1.y, "r");
	 	}
	gra.silnik.gracze[0]=temp_1;

}

function doKeyUp(e){
	 temp_2 = gra.silnik.gracze[0];
	 if(e.keyCode==87){
	 	temp_2.left=false;
	 }
	 if(e.keyCode==83){
	 	temp_2.right=false;
	 }
	 temp_2 = gra.silnik.gracze[0];
}

