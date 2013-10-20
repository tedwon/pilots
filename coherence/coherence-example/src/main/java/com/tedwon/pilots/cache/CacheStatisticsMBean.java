package com.tedwon.pilots.cache;

/**
 * Description.
 *
 * @author Ted Won
 * @since 1.0
 */
public interface CacheStatisticsMBean {

    /**
     * 케쉬 데이터 갯수.
     *
     * @return long 케쉬 데이터 갯수
     */
    long getEntryCount();
}
