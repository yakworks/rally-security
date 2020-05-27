package yakworks

import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import io.micronaut.http.client.HttpClient

@Integration
class RestSecurityLoginSpec extends Specification {
    @Shared
    HttpClient client

    @OnceBefore
    void init() {
        String baseUrl = "http://localhost:$serverPort"
        this.client = HttpClient.create(baseUrl.toURL())
    }

    void test_authorization_fail() {
        when:
        HttpRequest request = HttpRequest.GET('/api/book')
        HttpResponse<List<Map>> resp = client.toBlocking().exchange(request, Argument.of(List, Map))

        then:
        HttpClientResponseException ex = thrown()
        ex.message == "Access Denied"
    }

    @Ignore("TODO")
    void test_login() {
        when:
        HttpRequest request = HttpRequest.GET('/api/login')
        HttpResponse<List<Map>> resp = client.toBlocking().exchange(request, Argument.of(List, Map))

        then:
        true
    }
}
