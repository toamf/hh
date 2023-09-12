package diagram;

import models.Dragon;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public class Response implements Serializable {
    private final Status status;
    private String response = "";
    private Collection<Dragon> collection;

    public Response(Status status) {
        this.status = status;
    }

    public Response(Status status, String response) {
        this.status = status;
        this.response = response.trim();
    }

    public Response(Status status, String response, Collection<Dragon> collection) {
        this.status = status;
        this.response = response.trim();
        this.collection = collection.stream()
                .sorted(Comparator.comparing(Dragon::getId))
                .toList();
    }

    public Status getStatus() {
        return status;
    }

    public String getResponse() {
        return response;
    }

    public Collection<Dragon> getCollection() {
        return collection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response response1 = (Response) o;

        if (status != response1.status) return false;
        if (!Objects.equals(response, response1.response)) return false;
        return Objects.equals(collection, response1.collection);
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (response != null ? response.hashCode() : 0);
        result = 31 * result + (collection != null ? collection.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", response='" + response + '\'' +
                ", collection=" + collection +
                '}';
    }
}
