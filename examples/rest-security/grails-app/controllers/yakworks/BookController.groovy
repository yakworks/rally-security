package yakworks;

import grails.rest.RestfulController

class BookController extends RestfulController<Book> {

    BookController() {
        super(Book)
    }
}
