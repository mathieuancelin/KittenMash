package controllers

import play.api._
import play.api.mvc._
import models._

object Application extends Controller {
  
    def index = Action {
        val cats = Cat.randomCats()
        Ok( views.html.index( cats.head, cats.tail.head ) )
    }

    def stats = Action {
        Ok( views.html.stats( Cat.findAll().sortWith({ (cat1, cat2) => cat1.picked.compareTo(cat2.picked) > 0}) ) )
    }

    def pickACat( id: Long, panelId: Long ) = Action {
        Cat.findById(id).toRight( BadRequest( "Unkown kitten with id " + id ) ).fold( identity, { c =>
            c.picked()
            val random =  Cat.randomCats().filter(_.id != c.id).head
            panelId match {
                case 1 => Ok( views.html.index( c, random ) )
                case _ => Ok( views.html.index( random, c ) )
            }
        } ) 
    }
}