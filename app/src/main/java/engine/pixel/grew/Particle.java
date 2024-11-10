package engine.pixel.grew;

public class Particle {

    public int PosX;
    public int PosY;
    public double AccélérationY;
    public double AccélérationX;
    public double VelocityY;
    public double VelocityX;

    public int LiveTime;
    public int ParticleID;
    public int Color;

    public Particle(int PosX, int PosY, double VelocityX, double VelocityY, double AccélérationX, double AccélérationY, int Color, int ParticleID, int LiveTime) {
       //Toutes les variables d'une particule
        this.PosX= PosX;
        this.PosY= PosY;
        this.VelocityX= VelocityX;
        this.VelocityY= VelocityY;
        this.AccélérationX= AccélérationX;
        this.AccélérationY= AccélérationY;
        this.Color= Color;
        this.ParticleID= ParticleID;
        this.LiveTime= LiveTime;
    }
}
