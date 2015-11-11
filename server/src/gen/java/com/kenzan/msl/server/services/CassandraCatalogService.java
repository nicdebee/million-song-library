/*
 * Copyright 2015, Kenzan,  All rights reserved.
 */
package com.kenzan.msl.server.services;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.kenzan.msl.server.cassandra.CassandraConstants;
import com.kenzan.msl.server.cassandra.query.ArtistInfoQuery;
import com.kenzan.msl.server.cassandra.query.ArtistListQuery;
import com.kenzan.msl.server.dao.FacetWithChildrenDao;
import com.kenzan.msl.server.dao.translate.Translators;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import io.swagger.model.AlbumInfo;
import io.swagger.model.AlbumList;
import io.swagger.model.ArtistInfo;
import io.swagger.model.ArtistList;
import io.swagger.model.FacetInfoWithChildren;
import io.swagger.model.SongInfo;
import io.swagger.model.SongList;
import rx.Observable;

/**
 * Implementation of the CatalogSevice interface that retrieves its data from a
 * Cassandra cluster.
 *
 * @author billschwanitz
 */
public class CassandraCatalogService implements CatalogService {

	private Cluster cluster;
	private Session session;
	
	public CassandraCatalogService() {
		// TODO: Get the contact point from config param
		cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		
		// TODO: Get the keyspace from config param
		session = cluster.connect(CassandraConstants.MSL_KEYSPACE);
	}
	
    // ========================================================================================================== ALBUMS
    // =================================================================================================================

	/*
	 * Get browsing data for albums in the catalog.
	 * 
	 * This is a paginated query - it returns data one page at a time. The
	 * first page is retrieved by passing <code>null</code> as the
	 * <code>pagingState</code>. Subsequent pages are retrieved by passing
	 * the <code>pagingState</code> that accompanied the previously retrieved
	 * page.
	 * 
	 * The page size is determined by the <code>items</code> parameter when
	 * retrieving the first page. This value is used for all subsequent pages,
	 * (the <code>items</code> parameter is ignored when retrieving subsequent
	 * pages).
	 * 
	 * Data can be filtered using the <code>facets</code> parameter when
	 * retrieving the first page. This value is used for all subsequent pages,
	 * (the <code>facets</code> parameter is ignored when retrieving subsequent
	 * pages).
	 * 
	 * @param pagingState	Used for pagination control.
	 * 						To retrieve the first page, use <code>null</code>.
	 * 						To retrieve subsequent pages, use the
	 * 							<code>pagingState</code> that accompanied the
	 * 							previous page.
	 * @param items			Specifies the number of items to include in each page.
	 * 						This value is only necessary on the retrieval of the
	 * 							first page, and will be used for all subsequent
	 * 							pages.
	 * @param facets		Specifies a comma delimited list of search facet Ids
	 * 							to filter the results.
	 * 						Pass null or an empty string to not filter.
	 * @param userId		Specifies a user UUID identifying the currently logged-in
	 * 							user. Will be null for unauthenticated requests.
	 *   
	 * @return Observable<AlbumList>
	 */
	public Observable<AlbumList> browseAlbums(String pagingState, Integer items, String facets, String userId) {
		UUID pagingStateUuid = StringUtils.isEmpty(pagingState) ? null : UUID.fromString(pagingState);
		UUID userUuid = StringUtils.isEmpty(userId) ? null : UUID.fromString(userId);

		//return Observable.just(Translators.translate(AlbumListQuery.get(session, pagingStateUuid, items, facets, userUuid)));
		return null;
	}

	/*
	 * Get data on an album in the catalog.
	 *
	 * @param id	Specifies the UUID of the album to retrieve.
	 *  
	 * @return Observable<AlbumInfo>
	 */
	public Observable<AlbumInfo> getAlbum(String artistId, String userId) {
		UUID artistUuid = UUID.fromString(artistId);
		UUID userUuid = null == userId ? null : UUID.fromString(userId);
		
		//return Observable.just(Translators.translate(ArtistInfoQuery.get(session, userUuid, artistUuid)));
		return null;
	}

	
    // ========================================================================================================= ARTISTS
    // =================================================================================================================

	/*
	 * Get browsing data for artists in the catalog.
	 * 
	 * This is a paginated query - it returns data one page at a time. The
	 * first page is retrieved by passing <code>null</code> as the
	 * <code>pagingState</code>. Subsequent pages are retrieved by passing
	 * the <code>pagingState</code> that accompanied the previously retrieved
	 * page.
	 * 
	 * The page size is determined by the <code>items</code> parameter when
	 * retrieving the first page. This value is used for all subsequent pages,
	 * (the <code>items</code> parameter is ignored when retrieving subsequent
	 * pages).
	 * 
	 * Data can be filtered using the <code>facets</code> parameter when
	 * retrieving the first page. This value is used for all subsequent pages,
	 * (the <code>facets</code> parameter is ignored when retrieving subsequent
	 * pages).
	 * 
	 * @param pagingState	Used for pagination control.
	 * 						To retrieve the first page, use <code>null</code>.
	 * 						To retrieve subsequent pages, use the
	 * 							<code>pagingState</code> that accompanied the
	 * 							previous page.
	 * @param items			Specifies the number of items to include in each page.
	 * 						This value is only necessary on the retrieval of the
	 * 							first page, and will be used for all subsequent
	 * 							pages.
	 * @param facets		Specifies a comma delimited list of search facet Ids
	 * 							to filter the results.
	 * 						Pass null or an empty string to not filter.
	 * @param userId		Specifies a user UUID identifying the currently logged-in
	 * 							user. Will be null for unauthenticated requests.
	 *  
	 * @return Observable<ArtistList>
	 */
	public Observable<ArtistList> browseArtists(String pagingState, Integer items, String facets, String userId) {
		UUID pagingStateUuid = StringUtils.isEmpty(pagingState) ? null : UUID.fromString(pagingState);
		UUID userUuid = StringUtils.isEmpty(userId) ? null : UUID.fromString(userId);

		return Observable.just(Translators.translate(ArtistListQuery.get(session, pagingStateUuid, items, facets, userUuid)));
	}

	/*
	 * Get data on an artist in the catalog.
	 *
	 * @param id	Specifies the UUID of the artist to retrieve.
	 *  
	 * @return Observable<ArtistInfo>
	 */
	public Observable<ArtistInfo> getArtist(String artistId, String userId) {
		UUID artistUuid = UUID.fromString(artistId);
		UUID userUuid = (null == userId) ? null : UUID.fromString(userId);
		
		return Observable.just(Translators.translate(ArtistInfoQuery.get(session, userUuid, artistUuid)));
	}


    // ========================================================================================================== FACETS
    // =================================================================================================================

	/*
	 * Get data on a search facet in the catalog.
	 *
	 * @param id	Specifies the UUID of the facet to retrieve.
	 * 				Use "~" to retrieve the root facet of the hierarchy
	 *  
	 * @return Observable<FacetInfoWithChildren>
	 */
	public Observable<FacetInfoWithChildren> getFacet(String id) {
		FacetWithChildrenDao facetWithChildrenDao =  new MappingManager(session).mapper(FacetWithChildrenDao.class).get(id);
		return Observable.just(Translators.translate(facetWithChildrenDao));
	}


    // =========================================================================================================== SONGS
    // =================================================================================================================

	/*
	 * Get browsing data for songs in the catalog.
	 * 
	 * This is a paginated query - it returns data one page at a time. The
	 * first page is retrieved by passing <code>null</code> as the
	 * <code>pagingState</code>. Subsequent pages are retrieved by passing
	 * the <code>pagingState</code> that accompanied the previously retrieved
	 * page.
	 * 
	 * The page size is determined by the <code>items</code> parameter when
	 * retrieving the first page. This value is used for all subsequent pages,
	 * (the <code>items</code> parameter is ignored when retrieving subsequent
	 * pages).
	 * 
	 * Data can be filtered using the <code>facets</code> parameter when
	 * retrieving the first page. This value is used for all subsequent pages,
	 * (the <code>facets</code> parameter is ignored when retrieving subsequent
	 * pages).
	 * 
	 * @param pagingState	Used for pagination control.
	 * 						To retrieve the first page, use <code>null</code>.
	 * 						To retrieve subsequent pages, use the
	 * 							<code>pagingState</code> that accompanied the
	 * 							previous page.
	 * @param items			Specifies the number of items to include in each page.
	 * 						This value is only necessary on the retrieval of the
	 * 							first page, and will be used for all subsequent
	 * 							pages.
	 * @param facets		Specifies a comma delimited list of search facet Ids
	 * 							to filter the results.
	 * 						Pass null or an empty string to not filter.
	 * @param userId		Specifies a user UUID identifying the currently logged-in
	 * 							user. Will be null for unauthenticated requests.
	 *  
	 * @return Observable<SongList>
	 */
	public Observable<SongList> browseSongs(String pagingState, Integer items, String facets, String userId) {
		UUID pagingStateUuid = StringUtils.isEmpty(pagingState) ? null : UUID.fromString(pagingState);
		UUID userUuid = StringUtils.isEmpty(userId) ? null : UUID.fromString(userId);

		//return Observable.just(Translators.translate(SongListQuery.get(session, pagingStateUuid, items, facets, userUuid)));
		return null;
	}

	/*
	 * Get data on a song in the catalog.
	 *
	 * @param id	Specifies the UUID of the song to retrieve.
	 *  
	 * @return Observable<SongInfo>
	 */
	public Observable<SongInfo> getSong(String artistId, String userId) {
		UUID artistUuid = UUID.fromString(artistId);
		UUID userUuid = null == userId ? null : UUID.fromString(userId);
		
		//return Observable.just(Translators.translate(ArtistInfoQuery.get(session, userUuid, artistUuid)));
		return null;
	}
		
}
