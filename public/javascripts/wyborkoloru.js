var WyborKoloru = function(nasycenie, wartosc) {
    this.odcien = Math.random();
    this.nasycenie = nasycenie;
    this.wartosc = wartosc;
};

WyborKoloru.prototype.getColor  = function() {
    this.odcien += 0.618033988749895;
    this.odcien %= 1;

    return this.convertHSVToRGB(Math.floor(this.odcien * 360), this.nasycenie, this.wartosc);
};

WyborKoloru.prototype.reset = function() {
    this.odcien = Math.random();  
};

WyborKoloru.prototype.convertRGBToHex = function(color) {
    var r,
        g,
        b;
    
    r = color[0].toString(16);
    g = color[1].toString(16);
    b = color[2].toString(16);

    if (r.length < 2) {
        r = '0' + r;
    }

    if (g.length < 2) {
        g = '0' + g;
    }

    if (b.length < 2) {
        b = '0' + b;
    }

    return '#' + r + g + b;  
};

WyborKoloru.prototype.convertHSVToRGB = function(odcien, nasycenie, wartosc) {
    var h,
        hi,
        f,
        p,
        q,
        t,
        rgbResult = [];

    if (nasycenie === 0) {
        rgbResult[0] = wartosc;
        rgbResult[1] = wartosc;
        rgbResult[2] = wartosc;
    }

    h = odcien / 60;
    hi = Math.floor(h);
    f = h - hi;
    p = wartosc * (1 - nasycenie);
    q = wartosc * (1 - nasycenie * f);
    t = wartosc * (1 - nasycenie * (1 - f));

    if (hi === 0) {
        rgbResult = [wartosc, t, p];
    } else if (hi == 1) {
        rgbResult = [q, wartosc, p];
    } else if (hi == 2) {
        rgbResult = [p, wartosc, t];
    } else if (hi == 3) {
        rgbResult = [p, q, wartosc]; 
    } else if (hi == 4) {
        rgbResult = [t, p, wartosc];
    } else if (hi == 5) {
        rgbResult = [wartosc, p, q];
    }

    return [
        Math.floor(rgbResult[0] * 255),
        Math.floor(rgbResult[1] * 255),
        Math.floor(rgbResult[2] * 255)
    ];
};