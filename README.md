# Culture-buddies 
> Web application for people who want to experience cultural events with new buddies, 
 meet at concerts, museums, cinemas, theaters, talk about books, music, art...
                                                                                                            
## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Demo version](#demo-version)
* [Features](#features)
* [Status](#status)
* [Contact](#contact)
* [Database structure](#database-structure)
* [Figures](#figures)


## General info 
>The application allows users to meet new buddies with similar cultural interests and to arrange 
various cultural events. Users can, among others organize events, join other users' events, share opinions 
about read books, invite buddies, accept invitations from other buddies, remove and block unwanted buddies.


## Technologies
* Spring boot 2.3.3
* Spring data
* Spring Security
* Junit 6 
* Hibernate
* MySQl
* Thymeleaf
* REST API : Google Books Api, Google Places Api
## Demo version

## Features

### Ready

#### User (Buddy)
* Login 
* User registration with optional addition of a profile photo
* Sending a welcome message to the user's email address
* Profile page ([fig. 1](#fig-1-profile-page))
* Password change
* Profile picture change

##### books
* My books page ([fig. 2](#fig-2-my-books-page))
* Author page ([fig. 3](#fig-3-author-page))
* Searching for a new book using Google Books Api ([fig. 4](#fig-4-searching-for-a-new-book) and [fig. 5](#fig-5-searching-for-a-new-book))
* Adding and rating a new book ([fig. 6](#fig-6-adding-and-rating-a-new-book))
* Removing a book with confirmation ([fig. 7](#fig-7-removing-a-book-with-confirmation))
* Editing a rate of a book
* List of buddies' opinions about the book ([fig. 8](#fig-8-list-of-buddies-opinions-about-the-book))
* List of books recently added by buddies with their rating 

##### events
* My events page ([fig. 9](#fig-9-my-events-page))
* Buddy's events page ([fig. 10](#fig-10-buddys-events-page))
* Event info page with localization using Google Places Api
* Searching for an event to join by: title, username, type, city 
* Adding new event
* Deleting an event with confirmation
* Editing an event
* Joining other buddies' events
* Canceling participation in an event.
* List of all users' events
* List of events recently added by buddies  

##### buddies
* My buddies page ([fig. 11](#fig-11-my-buddies-page))
* My buddy page with buddy books and events([fig. 12](#fig-12-my-buddy-page))
* Searching for a new buddy by a username, interests
* Inviting a new buddy
* Accepting invitations from other buddies,
* removing unwanted buddies with confirmation
* blocking unwanted buddies with confirmation

### To-do list:
* Sending an email after changes to the event, password etc.
* Adding functionality in the area of movies and music
* Admin role (blocking users, removing inappropriate content, etc.)

## Status
Project is: _in progress_

## Contact
Created by [@dominika-czycz](https://github.com/dominika-czycz)
 
 [dominika-czycz@gmail.com](dominika.czycz@gmail.com) _feel free to contact!_


## Database structure

## Figures
###### Fig. 1 Profile page 
###### Fig. 2 My books page
###### Fig. 3 Author page 
###### Fig. 4 Searching for a new book 
###### Fig. 5 Searching for a new book 

###### Fig. 6 Adding and rating a new book 
###### Fig. 7 Removing a book with confirmation 
###### Fig. 8 List of buddies' opinions about the book 
###### Fig. 9 My events page 
###### Fig. 10 Buddy's events page 
###### Fig. 11 My buddies page 
###### Fig .12 My buddy page 

## Source
### profile pictures sources:
<span>Photo by <a href="https://unsplash.com/@tunagraphy?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">meriç tuna</a> on <a href="https://unsplash.com/s/photos/profile-picture?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Unsplash</a></span><span>Photo by <a href="https://unsplash.com/@benjeeeman?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Ben den Engelsen</a> on <a href="https://unsplash.com/s/photos/profile-picture?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Unsplash</a></span>
<span>Photo by <a href="https://unsplash.com/@sweetpagesco?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Sarah Brown</a> on <a href="https://unsplash.com/s/photos/profile-picture?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Unsplash</a></span>
<span>Photo by <a href="https://unsplash.com/@ericwebr?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Eric Weber</a> on <a href="https://unsplash.com/s/photos/profile-picture?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Unsplash</a></span>
<span>Photo by <a href="https://unsplash.com/@benjeeeman?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Ben den Engelsen</a> on <a href="https://unsplash.com/?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Unsplash</a></span>
<span>Photo by <a href="https://unsplash.com/@michaeldam?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Michael Dam</a> on <a href="https://unsplash.com/s/photos/profile-picture?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Unsplash</a></span>
<span>Photo by <a href="https://unsplash.com/@barborapoledn?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Barbora Polednová</a> on <a href="https://unsplash.com/s/photos/profile-picture?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Unsplash</a></span>
<span>Photo by <a href="https://unsplash.com/@gabrielsilverio?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Gabriel Silvério</a> on <a href="https://unsplash.com/s/photos/profile-picture?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Unsplash</a></span>
<span>Photo by <a href="https://unsplash.com/@raulangel?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Raul Angel</a> on <a href="https://unsplash.com/s/photos/profile-picture?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">Unsplash</a></span>


