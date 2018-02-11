package com.bargetor.nest.redis

import com.bargetor.nest.common.springmvc.SpringApplicationUtil
import org.redisson.api.RedissonClient
import java.util.concurrent.TimeUnit

class RedissonLockHelper{
    companion object {
        fun lock(key: String, waitTime: Long, leaseTime: Long, onLock: () -> Unit, onLockGetFail: () -> Unit, onLockOccupied: () -> Unit){
            val redissonClient = SpringApplicationUtil.getBean(RedissonClient::class.java) as RedissonClient
            val lock = redissonClient.getLock(key)
            var locked = false

            try {
                locked = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)
            } catch (e: InterruptedException) {
                //获取锁失败，直接退出
                onLockGetFail()
            }


            if (locked) {
                onLock()
                lock.unlock()
            } else {
                onLockOccupied()
            }
        }
    }
}