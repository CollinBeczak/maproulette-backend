package org.maproulette.cache

import java.util.concurrent.locks.{ReentrantReadWriteLock, ReadWriteLock}

import anorm._
import org.maproulette.data.Tag
import org.maproulette.data.dal.TagDAL
import play.api.db.DB
import play.api.Play.current
import scala.collection.mutable

/**
  * This is not currently a real cache manager, it will just store elements that have been loaded into memory.
  * Using this too much will most likely end up in OutOfMemory exceptions, so this needs to be readdressed
  * prior to a live version of this service
  *
  * @author cuthbertm
  */
object TagCacheManager extends CacheManager[Tag] {

  private val loadingLock:ReadWriteLock = new ReentrantReadWriteLock()
  override implicit protected val cache = mutable.Map[String, Tag]().empty

  // TODO: This is not the correct approach to just load everything into memory - this needs to be redone later
  def reloadTags = {
    loadingLock.writeLock().lock()
    try {
      DB.withConnection { implicit c =>
        cache.clear()
        SQL"""SELECT * FROM tags""".as(TagDAL.parser *).foreach(tag => add(tag.id, tag))
      }
    } finally {
      loadingLock.writeLock().unlock()
    }
  }
}
