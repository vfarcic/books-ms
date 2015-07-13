**TODO: Reference to series**
**TODO: Reference to microservices and front-end article**

In the [previous article](TODO) we created our first Polymer Web Component using [Test-Driven Development](http://technologyconversations.com/2014/09/30/test-driven-development-tdd/) approach. While all the specifications we wrote are fulfilled, there are still few things we should do to polish this component. The **tc-book-form** currently has the form with all input fields required by the back-end. It also has buttons to add or update and delete a book. Those buttons are calling **iron-ajax** Polymer component that sends requests to the back-end. Both front-end (in form of Web Component) and back-end are developed as a single microservice.

The next task in front of us is to polish the component. Later on in this series we'll create another component that will list all books. We'll also develop the code that will enable us to establish communication between those two components. Once we're done with both, we'll explore ways to integrate the microservice we're developing into a separate Web Application.

You can continue working on the code we wrote in the previous article. If you had trouble completing it, please checkout the [polymer-book-form](https://github.com/vfarcic/books-service/tree/polymer-book-form) branch of the  [books-service](https://github.com/vfarcic/books-service) repository. It contains the all the code from the previous article.

```bash
git checkout polymer-book-form
```

If you have trouble following the exercises in this article, the complete source code can be found in the [polymer-book-form-2](https://github.com/vfarcic/books-service/tree/polymer-book-form-2) branch of the [books-service](https://github.com/vfarcic/books-service) repository.

Specifying Book Retrieval
=========================

We should provide a way for users of our component to open a book. In in previous examples, we'll start with definition of another **iron-ajax** component. This time, we'll use it to retrieve a book. Back-end API for retrieving a book is similar to the DELETE request we already implemented. The request should be made to the **/api/v1/books/_id/[ID]** URL with **[ID]** replaced with the actual ID of the book we want to retrieve. The major difference is that this time the method should be **GET**.

The specification for the **getAjax** element is following.

[client/test/tc-book-form.html]
```javascript
describe('getAjax element', function() {

    it('is defined', function() {
        assert.isDefined(myEl.$.getAjax);
    });

});
```

The implementation is following.

[client/components/tc-books/tc-book-form.html]
```html
<iron-ajax id="getAjax"></iron-ajax>
```

Since the default method of the **iron-ajax** component is **GET**, there is no need to specify it explicitly.

With **getAjax** up-and-running, we should define a **open** function that users of our component can call. It should populate the form fields with the response from the server or, in case we pass **0** as book ID, clear the fields. For this function to operate correctly we should set the correct URL to **getAjax**, call **generateRequest** and display or hide the **Delete** button depending on whether **ID** is passed to the function or not.

[client/test/tc-book-form.html]
```javascript
describe('open function', function() {

    it('is defined', function() {
        assert.isDefined(myEl.open)
    });

    it('sets getAjax.url', function() {
        var bookId = 584;
        myEl.requestUrl = '/url/to/my/service';

        myEl.open(bookId);

        assert.equal(myEl.$.getAjax.url, myEl.requestUrl + '/_id/' + bookId);
    });

    it('calls getAjax.generateRequest', function() {
        var el = fixture('fixture');
        el.$.getAjax.generateRequest = sinon.mock();

        el.open(373);

        sinon.assert.calledOnce(el.$.getAjax.generateRequest);
    });

    it('resets _data when bookId is 0', function() {
        var data = {something: 'else'};
        myEl._data = data;

        myEl.open(0);

        assert.notEqual(myEl._data, data);
        assert.isUndefined(myEl._data._id);
    });

    it('displays delete element when bookId is greater than 0', function() {
        myEl.$.delete.hidden = true;

        myEl.open(429);

        assert.isFalse(myEl.$.delete.hidden);
    });

    it('hides delete element when bookId is 0', function() {
        myEl.$.delete.hidden = false;

        myEl.open(0);

        assert.isTrue(myEl.$.delete.hidden);
    });

});
```

The implementation is following.

[client/components/tc-books/tc-book-form.html]
```javascript
open: function(bookId) {
    if (bookId > 0) {
        this.$.getAjax.url = this.requestUrl + '/_id/' + bookId;
        this.$.getAjax.generateRequest();
        this.$.delete.hidden = false;
    } else {
        this._data = {description: ''};
        this.$.delete.hidden = true;
    }
}
```

Finally, we might want to define the default behaviour of our component. It should present the fields ready to insert a new book. Besides standard Web Component lifecycle callbacks, Polymer adds a new one called **ready**. It is called when all elements inside the component's local DOM have been configured.

In our case, it should be enough if the **ready** function call the **open** function we just implemented.

[client/test/tc-book-form.html]
```javascript
describe('ready function', function() {

    it('calls open function', function() {
        var el = fixture('fixture');
        el.open = sinon.mock();

        el.ready();

        sinon.assert.calledWith(el.open, 0);
    });

});
```

The implementation is following.

[client/components/tc-books/tc-book-form.html]
```javascript
ready: function() {
    this.open(0);
}
```

The only thing we have left inside this component is to specify what should be done when we receive responses from the back-end server.

Specifying HTTP Response Events
===============================

We are performing three different requests to the back-end server; GET, PUT and DELETE. Each of those responses can be successful with code **200** or contain an error. Whatever the response, we should notify users of our component so that they can decide what to do next. Notifications can be done by firing custom events. Polymer has the **fire** as one of it's [utility functions](https://www.polymer-project.org/1.0/docs/devguide/utility-functions.html).

We'll start a function that will handle **GET** response and continue with the rest **200** responses. Apart from making sure that function itself is defined, we should take the response and set it to the **_data** property that we defined in the previous article. Finally, we'll fire the **getBook** custom event.

[client/test/tc-book-form.html]
```javascript
describe('_handleGetResponse function', function() {

    it('is defined', function() {
        assert.isDefined(myEl._handleGetResponse);
    });

    it('sets _data property', function() {
        var expected = {something: 'not seen before'};
        var event = {
            detail: {
                response: expected
            }
        };
        myEl._data = {};

        myEl._handleGetResponse(event);

        assert.equal(myEl._data, expected);
    });
    
    it('fires getBook event', function() {
        var data = {something: 'else'};
        var response = {
            detail: {
                response: data
            }
        };
        var actual;
        var onGetBook = sinon.spy(function(e) {
            actual = e.detail;
        });
        myEl.addEventListener('getBook', onGetBook);

        myEl._handleGetResponse(response);

        sinon.assert.calledOnce(onGetBook);
        assert.equal(actual, myEl._data);
    });

});
```

The implementation is following.

[client/components/tc-books/tc-book-form.html]
```javascript
_handleGetResponse: function(e) {
    this._data = e.detail.response;
    this.fire('getBook', this._data);
}
```

Similar should be done with **PUT** and **DELETE** responses.

[client/test/tc-book-form.html]
```javascript
describe('_handlePutResponse', function() {

    it('is defined', function() {
        assert.isDefined(myEl._handlePutResponse);
    });

    it('fires putBook event', function() {
        myEl._data = {something: 'else'};
        var actual;
        var onPutBook = sinon.spy(function(e) {
            actual = e.detail;
        });
        myEl.addEventListener('putBook', onPutBook);

        myEl._handlePutResponse();

        sinon.assert.calledOnce(onPutBook);
        assert.equal(actual, myEl._data);
    });

    it('displays delete element', function() {
        myEl.$.delete.hidden = true;

        myEl._handlePutResponse();

        assert.isFalse(myEl.$.delete.hidden);
    });

});

describe('_handleDeleteResponse', function() {

    it('is defined', function() {
        assert.isDefined(myEl._handleDeleteResponse);
    });

    it('calls open function', function() {
        var el = fixture('fixture');
        el.open = sinon.mock();

        el._handleDeleteResponse();

        sinon.assert.calledWith(el.open, 0)
    });

    it('fires deleteBook event', function() {
        var data = {something: 'else'};
        myEl._data = data;
        var actual;
        var onDeleteBook = sinon.spy(function(e) {
            actual = e.detail;
        });
        myEl.addEventListener('deleteBook', onDeleteBook);

        myEl._handleDeleteResponse();

        sinon.assert.calledOnce(onDeleteBook);
        assert.equal(actual, data);
    });

});
```

The implementation is following.

[client/components/tc-books/tc-book-form.html]
```javascript
_handlePutResponse: function() {
    this.fire('putBook', this._data);
    this.$.delete.hidden = false;
},
_handleDeleteResponse: function() {
    var data = this._data;
    this.open(0);
    this.fire('deleteBook', data);
}
```

TODO
====

* Explain iron-ajax on-* functions.