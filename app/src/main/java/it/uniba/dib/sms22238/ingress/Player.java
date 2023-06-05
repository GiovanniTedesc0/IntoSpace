package it.uniba.dib.sms22238.ingress;

public class Player implements Comparable<Player> {

    private String name;
    private long punteggio;



    public Player(String name, long punteggio)
    {

        this.name=name;
        this.punteggio = punteggio;

    }

    public Player() {

    }

    public long getPunteggio() {
        return punteggio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }

    @Override
    public int compareTo(Player player) {

       return (int)player.punteggio - (int)this.punteggio;
    }
}
