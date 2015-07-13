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


TODO
====

* Styling
* Merge with master
* Add article link to the repository README.md
* Importing into Web Application
* nginx and deploymment
* Test on Windows and MAC