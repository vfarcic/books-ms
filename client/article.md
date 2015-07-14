**TODO: Reference to series**
**TODO: Reference to microservices and front-end article**

In the [previous article](TODO) we finished one Polymer Web Component. It contained a form that could be used to add, update and remove books y sending HTTP requests to the back-end server. We also explored few different ways to interact with the component from outside by calling it's functions.

In this article we'll create the second component that will be used to list all the books and send custom events to open an existing or a new book. Those events will not be sent directly to the component we built earlier but to the document the new component resides on. We'll get to that part in time...

The new component will be called **tc-book**.

You can continue working on the code we wrote in the previous article. If you had trouble completing it, please checkout the [polymer-book-form-2](https://github.com/vfarcic/books-service/tree/polymer-book-form-2) branch of the  [books-service](https://github.com/vfarcic/books-service) repository. It contains the all the code from the previous article.

```bash
git checkout polymer-book-form-2
```

If you have trouble following the exercises in this article, the complete source code can be found in the [polymer-books](https://github.com/vfarcic/books-service/tree/polymer-books) branch of the [books-service](https://github.com/vfarcic/books-service) repository.

We'll continue working using [Test-Driven Development](http://technologyconversations.com/2014/09/30/test-driven-development-tdd/) approach. The setup with Docker container that we used before is still valid. If, at any moment during the exercises provided below you feel the need to see the result, please open [http://localhost:8080/components/tc-books/demo/index.html](http://localhost:8080/components/tc-books/demo/index.html) in your favorite browser.

Let us start with high-level requirements.

Defining High-Level Requirements
================================

The idea behind this second component is to have a way to list books. Each of the listed books should be a button (or a link) that will send custom event with information about the selected book. The should also be a "New Book" button that will also send a custom event. As before, this component will be part of the same microservice as the previous one.

**TODO: Add sketch**

Defining Demo
=============

We'll start by adding the new component to the **Demo** page. Please open the **components/tc-books/demo/index.html** and add the following code.

[components/tc-books/demo/index.html]
```html
<div>
    <h1>Books List</h1>
    <div class="demo">
        <tc-books id="tc-books"></tc-books>
    </div>
</div>
```

We should also import the component.

[components/tc-books/demo/index.html]
```html
<head>
    ...
    <link rel="import" href="../tc-books.html">
    ...
</head>
```

From now on, everything we do inside this new component will be visible in the [demo page](http://localhost:8080/components/tc-books/demo/index.html).

We'll start with familiar elements like **iron-ajax** and **paper-toast** before diving into new stuff.

Specifying HTTP Request
=======================

We should have a way to query the back-end server for the list of books. The back-end server expects the **GET** request to be made on **/api/v1/books** URL. As with the previous component, we'll start by defining properties that we'll bind to the [iron-ajax](https://elements.polymer-project.org/elements/iron-ajax) element. 

[client/test/tc-books.html]
```javascript
describe('requestUrl property', function() {

    it('is defined', function() {
        assert.isDefined(myEl.requestUrl);
        assert.isString(myEl.requestUrl);
        assert.equal(myEl.requestUrl, '/api/v1/books');
    });

});
```

The implementation is following.

[client/components/tc-books/tc-books.html]
```javascript
properties: {
    requestUrl: {
        type: String,
        value: '/api/v1/books'
    }
}
```

With the property defined, we can move to the **iron-ajax** element. Unlike previous utilization of this element, this time we can set it to automatically perform the HTTP request. Later on there will be few other differences in the way we're using this element. That way you can experience various ways to accomplish the same result.

[client/test/tc-books.html]
```javascript
describe('ajax element', function() {

    it('is defined', function() {
        assert.isDefined(myEl.$.ajax);
    });

    it('sets auto to true', function() {
        assert.isTrue(myEl.$.ajax.auto);
    });

    it('binds url to requestUrl property', function() {
        assert.equal(myEl.$.ajax.url, myEl.requestUrl);
    });

});
```

The implementation is following.

[client/components/tc-books/tc-books.html]
```html
<iron-ajax id="ajax"
           auto
           url="[[requestUrl]]">
</iron-ajax>
```

Next, we should handle possible error responses. We'll use the same logic as before and implement [paper-toast](https://elements.polymer-project.org/elements/paper-toast), bind its **text** to a property and call the **open** function.

[client/test/tc-books.html]
```javascript
describe('requestErrorText property', function() {

    it('is defined', function() {
        assert.isDefined(myEl.requestErrorText);
        assert.isString(myEl.requestErrorText);
        assert.equal(myEl.requestErrorText, 'Something, somewhere, went wrong');
    });

});

describe('toast element', function() {

    it('is defined', function() {
        assert.isDefined(myEl.$.toast);
    });

    it('binds text to requestErrorText property', function() {
        myEl.requestErrorText = 'This text is displayed when request fails';

        assert.equal(myEl.$.toast.text, myEl.requestErrorText);
    });

});

describe('_handleError function', function() {

    it('calls toast show method', function() {
        myEl.$.toast.show = sinon.mock();

        myEl._handleError();

        sinon.assert.calledOnce(myEl.$.toast.show);
    });

});
```

The implementation is following.

[client/components/tc-books/tc-books.html]
```html
</template>
    ...
    <paper-toast id="toast" text="[[requestErrorText]]">
    </paper-toast>
    ...
</template>
```

[client/components/tc-books/tc-books.html]
```javascript
properties: {
    ...
    requestErrorText: {
        type: String,
        value: 'Something, somewhere, went wrong'
    }
},
...
_handleError: function() {
    this.$.toast.show();
}
```

Finally, we should add **on-error** to the **ajax* element. While we're at it, we'll also add **last-response** as well. When **iron-ajax** receives a response from the server, it will put it into the variable that can be accessed later on.

[client/components/tc-books/tc-books.html]
```html
<iron-ajax id="ajax"
           auto
           url="[[requestUrl]]"
           last-response="{{ajaxResponse}}"
           on-error="_handleError">
</iron-ajax>
```

Now that we are making a request to the back-end server and storing the (last) response to the **ajaxResponse** variable, we can work on displaying the listing.

Specifying Books Listing
========================

We can use the [Template Repeater](https://www.polymer-project.org/1.0/docs/devguide/templates.html#dom-repeat) to display the list of books. It binds to an array and it is written in format `<template id="dom-repeat">. 

The code could be following.

[client/components/tc-books/tc-books.html]
```html
<template id="books" is="dom-repeat" items="[[ajaxResponse]]">
    <div>
        <paper-button id="{{item.title}}"
                      on-tap="_openBook">[[item.title]]</paper-button>
    </div>
</template>
```

In out case, the template array is **ajaxResponse** set by the **ajax** element. Everything inside the template is repeated for each array element. We put [paper-button](https://elements.polymer-project.org/elements/paper-button) that, when clicked, will call the (not yet implemented) function **_openBook**.

Let us specify the **_openBook** function. Since the action of opening a book is in the other component and we cannot communicate with it directly, we are left with only one option. We can fire an event. Besides this limitation, it is a good way to accomplish **loose coupling**. We are building Web Components and leaving to other to decide which ones to use and how.

[client/test/tc-books.html]
```javascript
describe('_openBook function', function() {

    it('is defined', function() {
        assert.isDefined(myEl._openBook);
    });

    it('fires openBook event', function() {
        var item = {
            title: "An interesting book"
        };
        var event = {
            model: {
                item: item
            }
        };
        var actual;
        var onOpenBook = sinon.spy(function(e) {
            actual = e.detail;
        });
        myEl.addEventListener('openBook', onOpenBook);

        myEl._openBook(event);

        sinon.assert.calledOnce(onOpenBook);
        assert.equal(actual, item);
    });

});
```

The implementation is following.

[client/components/tc-books/tc-books.html]
```javascript
_openBook: function(e) {
    this.fire('openBook', e.model.item);
}
```

With the listing up and running, we might want to also add the **New Book** button.

Specifying New Book
===================

As with **Open Book** action, this one also belongs to the other component. The approach is similar to the one we did earlier. We'll add the **New Book** element, bind its text to a property and send a custom event whenever it is clicked.

[client/test/tc-books.html]
```javascript
describe('newBookText property', function() {

    it('is defined', function() {
        assert.isString(myEl.newBookText);
        assert.equal(myEl.newBookText, 'New Book');
    });

});

describe('_newBook function', function() {

    it('is defined', function() {
        assert.isDefined(myEl._newBook);
    });

    it('fires newBook event', function() {
        var onNewBook = sinon.mock();
        myEl.addEventListener('newBook', onNewBook);

        myEl._newBook();

        sinon.assert.calledOnce(onNewBook);
    });

});

describe('newBook element', function() {

    it('is defined', function() {
        assert.isDefined(myEl.$.newBook);
    });

    it('binds to newBookText property', function() {
        myEl.newBookText = 'New and Fancy Book';

        assert.equal(myEl.$.newBook.textContent.trim(), myEl.newBookText);
    });

    it('calls newBook function on click', function() {
        var el = fixture('fixture');
        el._newBook = sinon.mock();

        el.$.newBook.click();

        sinon.assert.calledOnce(el._newBook);
    });

});
```

The implementation is following.

[client/components/tc-books/tc-books.html]
```javascript
properties: {
    ...
    newBookText: {
        type: String,
        value: 'New Book'
    }
},
...
_newBook: function() {
    this.fire('newBook');
}
```

[client/components/tc-books/tc-books.html]
```html
<div style="margin-bottom: 5px;">
<paper-button id="newBook"
              raised
              on-tap="_newBook">[[newBookText]]</paper-button>
</div>
```

We are almost done with functional part of the component. The only thing left is **refresh** function.

Specifying Refresh
==================

Data can be changed through the **tc-book-form** component we developed earlier. We should have the option to refresh data in the **tc-book** component whenever the first one successfully requests the back-end to do some update.

[client/test/tc-books.html]
```javascript
describe('refresh function', function() {

    it('is defined', function() {
        assert.isDefined(myEl.refresh);
    });

    it('calls ajax.generateRequest function', function() {
        var el = fixture('fixture');
        el.$.ajax.generateRequest = sinon.mock();

        el.refresh();

        sinon.assert.calledOnce(el.$.ajax.generateRequest);
    });

});
```

The implementation is following.

[client/components/tc-books/tc-books.html]
```javascript
refresh: function() {
    this.$.ajax.generateRequest();
}
```

**TODO: Add screenshot**

We are (almost) done with the **tc-books** component.

To Be Continued
===============

We developed two separate components (**tc-book-form** and **tc-books**). Functionally, they are completely separated one from another. From deployment and architecture perspective, together with back-end, they for the part of the same microservice.

We are still missing a bit of polishing. In the next article we'll apply few styles and create a way for users of our components to have partial control of the look and feel. We'll improve our **Demo** page and create proper communication between our components.

Even those tasks do not conclude our work. We still have pending work on importing the components into a separate Web Application, take care of proxy with **nginx** and do the deployment.

**TODO: Link to the next article**

TODO
====

* Styling
* Merge with master
* Add article link to the repository README.md
* Importing into Web Application
* nginx and deployment
* Test on Windows and MAC