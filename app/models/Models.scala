package models

import anorm._ 
import play.api.db._
import anorm.SqlParser._
import play.api.Play.current
import java.util.Random

case class Cat( id: Long = -1L, url: String, picked:Long )

class EnhancedCat( cat: Cat ) {

    def save() = Cat.save( cat )

    def delete() = Cat.delete( cat.id )

    def exists() = Cat.exists( cat ) 

    def picked() = Cat.update( cat.id, Cat( cat.id, cat.url, cat.picked + 1 ) )
}

object Cat {

    implicit def Cat2EnhancedCat( cat: Cat ) = new EnhancedCat( cat )

    val simple = {
        get[Long]( "cat.id" ) ~ get[String]( "cat.url" ) ~ get[Long]( "cat.picked" ) map { 
            case id ~ url ~ picked => Cat( id, url, picked ) 
        }  
    }

    def randomCats() = DB.withConnection { implicit connection =>
        SQL( "select * from cat order by Rand()" ).as( Cat.simple * )
    }

    def findAll() = DB.withConnection { implicit connection =>
        SQL( "select * from cat order by id asc" ).as( Cat.simple * )
    }

    def findById( id:Long ) = DB.withConnection { implicit connection =>
        SQL( "select * from cat s where s.id = {id}" ).on( "id" -> id ).as( Cat.simple.singleOpt )
    } 

    def create( cat: Cat ) = DB.withConnection { implicit connection =>
        val id: Long = Cat.nextId()
        SQL( "insert into cat values ( {id}, {url}, {picked} )" ).on( "id" -> id, "url" -> cat.url, "picked" -> 0 ).executeUpdate()
        ( id, Cat( id, cat.url, cat.picked ) )
    }

    def save( cat:Cat ) = {
        if ( Cat.findById( cat.id ).isDefined ) {
            Cat.update( cat.id, cat )
        } else {
            Cat.create( cat )._2
        }
    }

    def delete( id: Long ) = DB.withConnection { implicit connection =>
        SQL( "delete from cat where id = {id}" ).on( "id" -> id ).executeUpdate()
    }

    def deleteAll() = DB.withConnection { implicit connection =>
        SQL( "delete from cat" ).executeUpdate()
    }

    def update( id: Long, cat: Cat ) = DB.withConnection { implicit connection =>
        SQL( "update cat set url = {url}, picked = {picked} where id = {id}" ).on( "id"-> id, "url" -> cat.url, "picked" -> cat.picked ).executeUpdate()
        Cat( id, cat.url, cat.picked )
    }

    def count() = DB.withConnection { implicit connection => 
        val firstRow = SQL( "select count(*) as s from cat" ).apply().head 
        firstRow[Long]( "s" )
    }

    def nextId() = DB.withConnection { implicit connection =>
        SQL( "select next value for cat_seq" ).as( scalar[Long].single )
    }

    def exists( id: Long ) = Cat.findById( id ).isDefined

    def exists( cat: Cat ) = Cat.findById( cat.id ).isDefined
}