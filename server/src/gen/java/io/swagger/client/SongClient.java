package io.swagger.client;

import io.swagger.model.SongInfo;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

/**
* Created by anram88 on 11/16/15.
*/
public class SongClient {

    private String baseUrl = "http://localhost:9000/msl";
    private ResteasyClient client;

    public SongClient() {
        client = new ResteasyClientBuilder().build();
    }

    public Response get (String id) {
        ResteasyWebTarget target = client.target(baseUrl + "/v1/catalogedge/");
        Response response = target
                .path("song/" + id)
                .request()
                .get();
        return response;
    }

    public Response browse(String facets) {
        ResteasyWebTarget target;
        if (!facets.isEmpty()){
            target = client.target(baseUrl + "/v1/catalogedge/browse/song?facets=" + facets);
        }else {
            target = client.target(baseUrl + "/v1/catalogedge/browse/song");
        }
        Response response = target.request().get();
        return response;
    }

    public Response addSong (String songId, String sessionToken) {
        ResteasyWebTarget target = client.target(baseUrl + "/v1/accountedge/users/mylibrary/addsong/" + songId);

        Response response = target
                .request()
                .header("sessionToken", sessionToken)
                .put(Entity.entity(songId, MediaType.APPLICATION_JSON));
        return response;
    }
}