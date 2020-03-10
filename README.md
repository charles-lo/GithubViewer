# Github Viewer
Github user Search App using Github API to demo.
* [All Users List](https://developer.github.com/v3/users/#get-all-users)
* [Pagination](https://developer.github.com/v3/#link-header)
* [Single User](https://developer.github.com/v3/users/#get-a-single-user)
* [Rate Limiting](https://developer.github.com/v3/#basic-authentication) - need to consider Rate limiting issues 

## Getting Started

Clone the project and run to get started.
this App has been implemented functionality :
1. MVVM architecture : retrofit & RxJava & LivaData to decouple View and data
2. Pagination(page size 20) has be implemented by Load More, Endless Scrolling user experience (Limited by 100 according to the spec, the limitation can be removed easily)
3. Adopt ObjectBox to cache detail user data for better user experience
4. search function in toolbar bar

## Screenshots

<img src="https://github.com/charles-lo/GithubViewer/blob/dev/snaps/screenshots_banner.gif" width="360">

## Built With

* [ObjectBox](https://objectbox.io/mobile/) - ObjectBox is an embedded, object-oriented database for Mobile Apps and IoT.  NoSQL, ACID-compliant DB is 10x faster than any alternative and takes only 1/10th of the code compared to SQLite. 
* [MVVM](https://developer.android.com/topic/libraries/architecture) - Use ViewModel and LiveData
* [Commons Collections](http://commons.apache.org/proper/commons-collections/) - The Java Collections Framework was a major addition in JDK 1.2. It added many powerful data structures that accelerate development of most significant Java applications. Since that time it has become the recognised standard for collection handling in Java.
* [RxJava](https://github.com/ReactiveX/RxJava) - RxJava is a Java VM implementation of Reactive Extensions: a library for composing asynchronous and event-based programs by using observable sequences.
* [Retrofit](https://github.com/square/retrofit) - Network Calls
* [GSON](https://github.com/google/gson) - For JSON to JAVA Deserialization
* [Shimmer Recycler View](https://github.com/sharish/ShimmerRecyclerView) - To show shimmering effect in recycler view while loading
* [Glide](https://github.com/bumptech/glide) - Image Loading

## Author

* **[Charles Lo](https://www.linkedin.com/in/charles-lo-aa296712/)**
