import play.api._
import java.io._
import models._

object Global extends GlobalSettings {

    val IMG_FILE = "public/img"
    val IMG_URL = "/assets/img/"
  
    override def onStart( app: Application ) {
        Cat.deleteAll()
        new File( IMG_FILE ).listFiles().foreach { img =>
            Cat( 0, IMG_URL + img.getName(), 0 ).save()
        }
    }
}