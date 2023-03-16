package main;
import java.awt.BorderLayout;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PlayerPanel extends JPanel {

    //private File vlcInstallPath = new File("D:/vlc");
    EmbeddedMediaPlayer player;

    public PlayerPanel() {
        EmbeddedMediaPlayerComponent videoCanvas = new EmbeddedMediaPlayerComponent();
        this.setLayout(new BorderLayout());
        this.add(videoCanvas, BorderLayout.CENTER);
        this.player = videoCanvas.mediaPlayer();
    }

    public void play(String media) {
    	if (player.status().isPlaying()) {
            player.controls().stop();
        }
        player.media().play(media);
        
    }

	public void close() {
		player.release();
		
	}
}


