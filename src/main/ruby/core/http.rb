# Copyright 2011 VMware, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

require 'core/streams'
require 'core/ssl_support'

module Nodex

  # An HTTP server.
  # The server supports both HTTP requests and HTML5 websockets and passes these to the user via the appropriate handlers.
  # Instances of HttpServer can only be used from the event loop that created it.
  #
  # @author {http://tfox.org Tim Fox}
  class HttpServer

    include SSLSupport

    # Create a new HttpServer
    def initialize
      @j_del = org.nodex.java.core.http.HttpServer.new
    end

    # Set the HTTP request handler for the server.
    # As HTTP requests arrive on the server a new {HttpServerRequest} instance will be created and passed to the handler.
    # @param [Proc] proc A proc to be used as the handler
    # @param [Block] hndlr A block to be used as the handler
    def request_handler(proc = nil, &hndlr)
      hndlr = proc if proc
      @j_del.requestHandler { |j_del| hndlr.call(HttpServerRequest.new(j_del)) }
      self
    end

    # Set the websocket handler for the server.
    # As websocket requests arrive on the server and are accepted a new {Websocket} instance will be created and passed to the handler.
    # @param [Proc] proc A proc to be used as the handler
    # @param [Block] hndlr A block to be used as the handler
    def websocket_handler(proc = nil, &hndlr)
      hndlr = proc if proc
      @j_del.websocketHandler { |ws| hndlr.call(Websocket.new(ws)) }
      self
    end

    # Instruct the server to listen for incoming connections.
    # @param [FixNum] port. The port to listen on.
    # @param [FixNum] host. The host name or ip address to listen on.
    def listen(port, host = "0.0.0.0")
      @j_del.listen(port, host)
      self
    end

    # Client authentication is an extra level of security in SSL, and requires clients to provide client certificates.
    # Those certificates must be added to the server trust store.
    # @param [Boolean] val. If true then the server will request client authentication from any connecting clients, if they
    # do not authenticate then they will not make a connection.
    def client_auth_required=(val)
      @j_del.setClientAuthRequired(val)
      self
    end

    # Close the server. The handler will be called when the close is complete.
    def close(&hndlr)
      @j_del.close(hndlr)
    end
  end

  # An HTTP client.
  # A client maintains a pool of connections to a specific host, at a specific port. The HTTP connections can act
  # as pipelines for HTTP requests.
  # It is used as a factory for {HttpClientRequest} instances which encapsulate the actual HTTP requests. It is also
  # used as a factory for HTML5 {Websocket websockets}.
  # The client is thread-safe and can be safely shared by different event loops.
  #
  # @author {http://tfox.org Tim Fox}
  class HttpClient

    include SSLSupport

    # Create a new HttpClient
    def initialize
      @j_del = org.nodex.java.core.http.HttpClient.new
    end

    # Set the exception handler.
    # @param [Proc] proc A proc to be used as the handler
    # @param [Block] hndlr A block to be used as the handler
    def exception_handler(proc = nil, &hndlr)
      hndlr = proc if proc
      @j_del.exceptionHandler(hndlr)
      self
    end

    # Set the maximum pool size.
    # The client will maintain up to this number of HTTP connections in an internal pool
    # @param [FixNum] val. The maximum number of connections (default to 1).
    def max_pool_size=(val)
      @j_del.setMaxPoolSize(val)
      self
    end

    # @return [FixNum] The maxium number of connections this client will pool.
    def max_pool_size
      @j_del.getMaxPoolSize
    end

    # If val is true then, after the request has ended the connection will be returned to the pool
    # where it can be used by another request. In this manner, many HTTP requests can be pipe-lined over an HTTP connection.
    # Keep alive connections will not be closed until the {#close} method is invoked.
    # If val is false then a new connection will be created for each request and it won't ever go in the pool,
    # the connection will closed after the response has been received. Even with no keep alive, the client will not allow more
    # than {#max_pool_size} connections to be created at any one time.
    # @param [Boolean] val. The value to use for keep_alive
    def keep_alive=(val)
      @j_del.setTCPKeepAlive(val)
      self
    end

    # Should the client trust ALL server certificates?
    # @param [Boolean] val. If val is set to true then the client will trust ALL server certificates and will not attempt to authenticate them
    # against it's local client trust store. The default value is false.
    # Use this method with caution!
    def trust_all=(val)
      @j_del.setTrustAll(val)
      self
    end

    # Set the port that the client will attempt to connect to on the server on. The default value is 80
    # @param [FixNum] val. The port value.
    def port=(val)
      @j_del.setPort(val)
      self
    end

    # Set the host name or ip address that the client will attempt to connect to on the server on.
    # @param [String] host. The host name or ip address to connect to.
    def host=(val)
      @j_del.setHost(val)
      self
    end

    # Attempt to connect an HTML5 websocket to the specified URI.
    # The connect is done asynchronously and the handler is called with a Completion containing a {Websocket} on success.
    # @param [String] uri. A relative URI where to connect the websocket on the host, e.g. /some/path
    # @param [Block] hndlr. The handler to be called with the {Websocket}
    def connect_web_socket(uri, &hndlr)
      @j_del.connectWebsocket(uri) { |j_ws| hndlr.call(Websocket.new(j_ws)) }
    end

    # This is a quick version of the {#get} method where you do not want to do anything with the request
    # before sending.
    # Normally with any of the HTTP methods you create the request then when you are ready to send it you call
    # {HttpClientRequest#end} on it. With this method the request is immediately sent.
    # When an HTTP response is received from the server the handler is called passing in the response.
    # @param [String] uri. A relative URI where to perform the GET on the server.
    # @param [Hash] headers. A Hash of headers to pass with the request.
    # @param [Block] hndlr. The handler to be called with the {HttpClientResponse}
    def get_now(uri, headers = nil, &hndlr)
      @j_del.getNow(uri, headers, resp_handler(hndlr))
    end

    # This method returns an {HttpClientRequest} instance which represents an HTTP OPTIONS request with the specified uri.
    # When an HTTP response is received from the server the handler is called passing in the response.
    # @param [String] uri. A relative URI where to perform the OPTIONS on the server.
    # @param [Block] hndlr. The handler to be called with the {HttpClientResponse}
    def options(uri, &hndlr)
      HttpClientRequest.new(@j_del.options(uri, resp_handler(hndlr)))
    end

    # This method returns an {HttpClientRequest} instance which represents an HTTP GET request with the specified uri.
    # When an HTTP response is received from the server the handler is called passing in the response.
    # @param [String] uri. A relative URI where to perform the GET on the server.
    # @param [Block] hndlr. The handler to be called with the {HttpClientResponse}
    def get(uri, &hndlr)
      HttpClientRequest.new(@j_del.get(uri, resp_handler(hndlr)))
    end

    # This method returns an {HttpClientRequest} instance which represents an HTTP HEAD request with the specified uri.
    # When an HTTP response is received from the server the handler is called passing in the response.
    # @param [String] uri. A relative URI where to perform the HEAD on the server.
    # @param [Block] hndlr. The handler to be called with the {HttpClientResponse}
    def head(uri, &hndlr)
      HttpClientRequest.new(@j_del.head(uri, resp_handler(hndlr)))
    end

    # This method returns an {HttpClientRequest} instance which represents an HTTP POST request with the specified uri.
    # When an HTTP response is received from the server the handler is called passing in the response.
    # @param [String] uri. A relative URI where to perform the POST on the server.
    # @param [Block] hndlr. The handler to be called with the {HttpClientResponse}
    def post(uri, &hndlr)
      HttpClientRequest.new(@j_del.post(uri, resp_handler(hndlr)))
    end

    # This method returns an {HttpClientRequest} instance which represents an HTTP PUT request with the specified uri.
    # When an HTTP response is received from the server the handler is called passing in the response.
    # @param [String] uri. A relative URI where to perform the PUT on the server.
    # @param [Block] hndlr. The handler to be called with the {HttpClientResponse}
    def put(uri, &hndlr)
      HttpClientRequest.new(@j_del.put(uri, resp_handler(hndlr)))
    end

    # This method returns an {HttpClientRequest} instance which represents an HTTP DELETE request with the specified uri.
    # When an HTTP response is received from the server the handler is called passing in the response.
    # @param [String] uri. A relative URI where to perform the DELETE on the server.
    # @param [Block] hndlr. The handler to be called with the {HttpClientResponse}
    def delete(uri, &hndlr)
      HttpClientRequest.new(@j_del.delete(uri, resp_handler(hndlr)))
    end

    # This method returns an {HttpClientRequest} instance which represents an HTTP TRACE request with the specified uri.
    # When an HTTP response is received from the server the handler is called passing in the response.
    # @param [String] uri. A relative URI where to perform the TRACE on the server.
    # @param [Block] hndlr. The handler to be called with the {HttpClientResponse}
    def trace(uri, &hndlr)
      HttpClientRequest.new(@j_del.trace(uri, resp_handler(hndlr)))
    end

    # This method returns an {HttpClientRequest} instance which represents an HTTP CONNECT request with the specified uri.
    # When an HTTP response is received from the server the handler is called passing in the response.
    # @param [String] uri. A relative URI where to perform the CONNECT on the server.
    # @param [Block] hndlr. The handler to be called with the {HttpClientResponse}
    def connect(uri, &hndlr)
      HttpClientRequest.new(@j_del.connect(uri, resp_handler(hndlr)))
    end

    # This method returns an {HttpClientRequest} instance which represents an HTTP request with the specified method and uri.
    # When an HTTP response is received from the server the handler is called passing in the response.
    # @param [String] method. The HTTP method. Can be one of OPTIONS, HEAD, GET, POST, PUT, DELETE, TRACE, CONNECT.
    # @param [String] uri. A relative URI where to perform the OPTIONS on the server.
    # @param [Block] hndlr. The handler to be called with the {HttpClientResponse}
    def request(method, uri, &hndlr)
      HttpClientRequest.new(@j_del.request(method, uri, resp_handler(hndlr)))
    end

    # Close the client. Any unclosed connections will be closed.
    def close
      @j_del.close
    end

    # @private
    def resp_handler(hndlr)
      Proc.new { |j_del| hndlr.call(HttpClientResponse.new(j_del)) }
    end

    private :resp_handler

  end

  # Encapsulates a client-side HTTP request.
  #
  # Instances of this class are created by an {HttpClient} instance, via one of the methods corresponding to the
  # specific HTTP methods, or the generic {HttpClient#request} method.
  #
  # Once an instance of this class has been obtained, headers can be set on it, and data can be written to its body,
  # if required. Once you are ready to send the request, the {#end} method must called.
  #
  # Nothing is sent until the request has been internally assigned an HTTP connection. The {HttpClient} instance
  # will return an instance of this class immediately, even if there are no HTTP connections available in the pool. Any requests
  # sent before a connection is assigned will be queued internally and actually sent when an HTTP connection becomes
  # available from the pool.
  #
  # The headers of the request are actually sent either when the {#end} method is called, or, when the first
  # part of the body is written, whichever occurs first.
  #
  # This class supports both chunked and non-chunked HTTP.
  #
  # This class can only be used from the event loop that created it.
  #
  # An example of using this class is as follows:
  #
  # @example
  #
  #   req = httpClient.post("/some-url") do |response|
  #     puts "Got response #{response.status_code}"
  #   end
  #
  #   req.put_header("some-header", "hello")
  #
  #   req.chunked = true
  #   req.write(Buffer.create_from_str("chunk of body 1");
  #   req.write(Buffer.create_from_str("chunk of body 2");
  #
  #   req.end(); # This actually sends the request
  #
  # @author {http://tfox.org Tim Fox}
  class HttpClientRequest

    include WriteStream

    # @private
    def initialize(j_del)
      @j_del = j_del
    end

    # Inserts a header into the request.
    # @param [String] key The header key
    # @param [Object] value The header value. to_s will be called on the value to determine the actual String value to insert.
    # @return [HttpClientRequest] self So multiple operations can be chained.
    def put_header(key, value)
      @j_del.putHeader(key, value.to_s)
      self
    end

    # Inserts a Hash of headers into the request.
    # @param [Hash] headers. Headers to insert. to_s will be called on each value to determine the actual String value to insert.
    # @return [HttpClientRequest] self So multiple operations can be chained.
    def put_all_headers(headers)
      headers.each_pair do |k, v|
        @j_del.putHeader(k, v.to_s)
      end
      self
    end

    # Write a [Buffer] to the request body.
    # @param [Buffer] chunk. The buffer to write.
    # @param [Block] hndlr. The handler will be called when the buffer has actually been written to the wire.
    # @return [HttpClientRequest] self So multiple operations can be chained.
    def write_buffer(chunk, &hndlr)
      @j_del.writeBuffer(chunk._to_java_buffer)
      self
    end

    # Write a [String] to the request body.
    # @param [String] str. The string to write.
    # @param [String] enc. The encoding to use.
    # @param [Block] hndlr. The handler will be called when the buffer has actually been written to the wire.
    # @return [HttpClientRequest] self So multiple operations can be chained.
    def write_str(str, enc = "UTF-8", &hndlr)
      @j_del.write(str, enc)
      self
    end

    # Forces the head of the request to be written before {#end} is called on the request. This is normally used
    # to implement HTTP 100-continue handling, see {#continue_handler} for more information.
    # @return [HttpClientRequest] self So multiple operations can be chained.
    def send_head
      @j_del.sendHead
      self
    end

    # Ends the request. If no data has been written to the request body, and {#send_head} has not been called then
    # the actual request won't get written until this method gets called.
    # Once the request has ended, it cannot be used any more, and if keep alive is true the underlying connection will
    # be returned to the {HttpClient} pool so it can be assigned to another request.
    def end
      @j_del.end
    end

    # Same as {#write_buffer_and_end} but writes a String
    # @param [String] str The String to write
    # @param [String] enc The encoding
    def write_str_and_end(str, enc = "UTF-8")
      @j_del.end(str, enc)
    end

    # Same as {#end} but writes some data to the response body before ending. If the response is not chunked and
    # no other data has been written then the Content-Length header will be automatically set
    # @param [Buffer] chunk The Buffer to write
    def write_buffer_and_end(chunk)
      @j_del.end(chunk._to_java_buffer)
    end

    # Sets whether the request should used HTTP chunked encoding or not.
    # @param [Boolean] val. If val is true, this request will use HTTP chunked encoding, and each call to write to the body
    # will correspond to a new HTTP chunk sent on the wire. If chunked encoding is used the HTTP header
    # 'Transfer-Encoding' with a value of 'Chunked' will be automatically inserted in the request.
    # If chunked is false, this request will not use HTTP chunked encoding, and therefore if any data is written the
    # body of the request, the total size of that data must be set in the 'Content-Length' header before any
    # data is written to the request body. If no data is written, then a 'Content-Length' header with a value of '0'
    # will be automatically inserted when the request is sent.
    # @return [HttpClientRequest] self So multiple operations can be chained.
    def chunked=(val)
      @j_del.setChunked(val)
      self
    end

    # If you send an HTTP request with the header 'Expect' set to the value '100-continue'
    # and the server responds with an interim HTTP response with a status code of '100' and a continue handler
    # has been set using this method, then the handler will be called.
    # You can then continue to write data to the request body and later end it. This is normally used in conjunction with
    # the {#send_head} method to force the request header to be written before the request has ended.
    # @param [Proc] proc. The handler
    # @param [Block] hndlr. The handler
    def continue_handler(proc = nil, &hndlr)
      hndlr = proc if proc
      @j_del.continueHandler(hndlr)
    end

  end

  # Encapsulates a client-side HTTP response.
  #
  # An instance of this class is provided to the user via a handler that was specified when one of the
  # HTTP method operations, or the generic {HttpClient#request} method was called on an instance of {HttpClient}.
  #
  # Instances of this class can only be used from the event loop thread which created the corresponding {HttpClientRequest}.</p>
  #
  # @author {http://tfox.org Tim Fox}
  class HttpClientResponse

    include ReadStream

    # @private
    def initialize(j_del)
      @j_del = j_del
      @status_code = j_del.statusCode
    end

    # @return [FixNum] the HTTP status code of the response.
    def status_code
      @status_code
    end

    # Get a header value
    # @param [String] key. The key of the header.
    # @return [String] the header value.
    def header(key)
      @j_del.getHeader(key)
    end

    # Get all the headers in the response.
    # If the response contains multiple headers with the same key, the values
    # will be concatenated together into a single header with the same key value, with each value separated by a comma,
    # as specified by {http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2}.
    # @return [Hash]. A Hash of headers.
    def headers
      if @headers == nil
        hdrs = @j_del.getHeaders
        iter = hdrs.entrySet.iterator
        @headers = {}
        while iter.hasNext
          entry = iter.next
          @headers[entry.getKey] = entry.getValue
        end
      end
      @headers
    end

    # Get all the header names from the response.
    # @return [Set]. A Set of header names
    def header_names
      if @header_names == nil
        names = @j_del.getHeaderNames
        iter = names.iterator
        @header_names = Set.new
        while iter.hasNext
          name = iter.next
          @header_names.add(name)
        end
      end
      @header_names
    end

    # Returns a trailer value, or nil if no such trailer exists.
    # Trailers will only be available in the response if the server has sent a HTTP chunked response where headers have
    # been inserted by the server on the last chunk. In such a case they won't be available on the client until the last chunk has
    # been received.
    # @param [String] key. The key of the trailer.
    def trailer(key)
      @j_del.getTrailer(key)
    end

    # Get all the trailers in the response.
    # If the response contains multiple trailers with the same key, the values
    # will be concatenated together into a single header with the same key value, with each value separated by a comma,
    # as specified by {http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2}.
    # Trailers will only be available in the response if the server has sent a HTTP chunked response where headers have
    # been inserted by the server on the last chunk. In such a case they won't be available on the client until the last chunk has
    # been received.
    # @return [Hash]. A Hash of trailers.
    def trailers
      if @trailers == nil
        hdrs = @j_del.getHeaders
        iter = hdrs.iterator
        @trailers = {}
        while iter.hasNext
          entry = iter.next
          @trailers[entry.getkey] = entry.getValue
        end
      end
      @trailers
    end

    # Get all the trailer names from the response.
    # Trailers will only be available in the response if the server has sent a HTTP chunked response where headers have
    # been inserted by the server on the last chunk. In such a case they won't be available on the client until the last chunk has
    # been received.
    # @return [Set]. A Set of trailer names
    def trailer_names
      if @trailer_names == nil
        names = @j_del.getTrailerNames
        iter = names.iterator
        @trailer_names = Set.new
        while iter.hasNext
          name = iter.next
          @trailer_names.add(name)
        end
      end
      @trailer_names
    end

  end

  # Encapsulates a server-side HTTP request.
  #
  # An instance of this class is created for each request that is handled by the server and is passed to the user via the
  # handler specified using {HttpServer#request_handler}.
  #
  # On creation a new execution context is assigned to each instance and an event loop is allocated to it from one
  # of the available ones. The instance must only be called from that event loop.
  #
  # Each instance of this class is associated with a corresponding {HttpServerResponse} instance via the field {#response}.
  #
  # @author {http://tfox.org Tim Fox}
  class HttpServerRequest

    include ReadStream

    # @private
    def initialize(j_del)
      @j_del = j_del
      @resp = HttpServerResponse.new(@j_del.response)
    end

    # @return [String] The HTTP method, one of HEAD, OPTIONS, GET, POST, PUT, DELETE, CONNECT, TRACE
    def method
      @j_del.method
    end

    # @return [String] The uri of the request. For example 'http://www.somedomain.com/somepath/somemorepath/somresource.foo?someparam=32&someotherparam=x'
    def uri
      @j_del.uri
    end

    # @return [HttpServerResponse] The response. Each instance of this class has an {HttpServerResponse} instance attached to it. This is used
    # to send the response back to the client.
    def response
      @resp
    end

    # Get a header value
    # @param [String] key. The key of the header.
    # @return [String] the header value.
    def header(key)
      @j_del.getHeader(key)
    end

     # Get all the headers in the response.
    # If the response contains multiple headers with the same key, the values
    # will be concatenated together into a single header with the same key value, with each value separated by a comma,
    # as specified by {http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2}.
    # @return [Hash]. A Hash of headers.
    def headers
      if (@headers == nil)
        hdrs = @j_del.getHeaders
        iter = hdrs.entrySet.iterator
        @headers = {}
        while iter.hasNext
          entry = iter.next
          @headers[entry.getKey] = entry.getValue
        end
      end
      @headers
    end

    # Get all the header names from the response.
    # @return [Set]. A Set of header names
    def header_names
      if (@header_names == nil)
        names = @j_del.getHeaderNames
        iter = names.iterator
        @header_names = Set.new
        while iter.hasNext
          name = iter.next
          @header_names.add(name)
        end
      end
      @header_names
    end

  end

  # Encapsulates a server-side HTTP response.
  #
  # An instance of this class is created and associated to every instance of {HttpServerRequest} that is created.
  #
  # It allows the developer to control the HTTP response that is sent back to the client for the corresponding HTTP
  # request. It contains methods that allow HTTP headers and trailers to be set, and for a body to be written out
  # to the response.
  #
  # It also allows a file to be streamed by the kernel directly from disk to the outgoing HTTP connection,
  # bypassing user space altogether (where supported by the underlying operating system). This is a very efficient way of
  # serving files from the server since buffers do not have to be read one by one from the file and written to the outgoing
  # socket.
  #
  # @author {http://tfox.org Tim Fox}
  class HttpServerResponse

    include WriteStream

    # @private
    def initialize(j_del)
      @j_del = j_del
    end

    # Set the status code of the response. Default is 200
    # @param [FixNum] val. The HTTP status code.
    def status_code=(val)
      @j_del.statusCode = val
    end

    # Inserts a header into the response.
    # @param [String] key The header key
    # @param [Object] value The header value. to_s will be called on the value to determine the actual String value to insert.
    # @return [HttpServerResponse] self So multiple operations can be chained.
    def put_header(key, value)
      @j_del.putHeader(key, value.to_s)
      self
    end

    # Inserts a Hash of headers into the response.
    # @param [Hash] headers. Headers to insert. to_s will be called on each value to determine the actual String value to insert.
    # @return [HttpServerResponse] self So multiple operations can be chained.
    def put_all_headers(headers)
      headers.each_pair do |k, v|
        @j_del.putHeader(k, v)
      end
      self
    end

    # Inserts a trailer into the response. Trailers are only sent if you are using a HTTP chunked response.
    # @param [String] key The trailer key
    # @param [Object] value The trailer value. to_s will be called on the value to determine the actual String value to insert.
    # @return [HttpServerResponse] self So multiple operations can be chained.
    def put_trailer(key, value)
      @j_del.putTrailer(key, value.to_s)
      self
    end

    # Inserts a Hash of trailers into the response. Trailers are only sent if you are using a HTTP chunked response.
    # @param [Hash] trailers. Trailers to insert. to_s will be called on each value to determine the actual String value to insert.
    # @return [HttpServerResponse] self So multiple operations can be chained.
    def put_all_trailers(headers)
      trailers.each_pair do |k, v|
        @j_del.putTrailer(k, v)
      end
      self
    end

    # Write a buffer to the response. The handler will be called when the buffer has actually been written to the wire.
    # @param [Buffer] chunk. The buffer to write
    # @param [Block] hndlr. The handler
    # @return [HttpServerResponse] self So multiple operations can be chained.
    def write_buffer(chunk, &hndlr)
      @j_del.writeBuffer(chunk._to_java_buffer)
      self
    end

    # Write a String to the response. The handler will be called when the String has actually been written to the wire.
    # @param [String] str. The string to write
    # @param [String] enc. Encoding to use.
    # @param [Block] hndlr. The handler
    # @return [HttpServerResponse] self So multiple operations can be chained.
    def write_str(str, enc = "UTF-8", &hndlr)
      @j_del.write(str, enc)
      self
    end

    # Tell the kernel to stream a file directly from disk to the outgoing connection, bypassing userspace altogether
    # (where supported by the underlying operating system. This is a very efficient way to serve files.
    # @param [String] path. Path to file to send.
    # @return [HttpServerResponse] self So multiple operations can be chained.
    def send_file(path)
      @j_del.sendFile(path)
      self
    end

    # Sets whether this response uses HTTP chunked encoding or not.
    # @param [Boolean] val. If val is true, this response will use HTTP chunked encoding, and each call to write to the body
    # will correspond to a new HTTP chunk sent on the wire. If chunked encoding is used the HTTP header
    # 'Transfer-Encoding' with a value of 'Chunked' will be automatically inserted in the response.
    # If chunked is false, this response will not use HTTP chunked encoding, and therefore if any data is written the
    # body of the response, the total size of that data must be set in the 'Content-Length' header <before< any
    # data is written to the response body. If no data is written, then a 'Content-Length' header with a value of '0''
    # will be automatically inserted when the response is sent.
    # An HTTP chunked response is typically used when you do not know the total size of the request body up front.
    # @return [HttpServerResponse] self So multiple operations can be chained.
    def chunked=(val)
      @j_del.setChunked(val)
      self
    end

    # Ends the response. If no data has been written to the response body, the actual response won't get written until this method gets called.
    # Once the response has ended, it cannot be used any more, and if keep alive is true the underlying connection will
    # be closed.
    def end
      @j_del.end
    end

    # Same as {#write_buffer_and_end} but writes a String
    # @param [String] str The String to write
    # @param [String] enc The encoding
    def write_str_and_end(str, enc = "UTF-8")
      @j_del.end(str, enc)
    end

    # Same as {#end} but writes some data to the response body before ending. If the response is not chunked and
    # no other data has been written then the Content-Length header will be automatically set
    # @param [Buffer] chunk The Buffer to write
    def write_buffer_and_end(chunk)
      @j_del.end(chunk._to_java_buffer)
    end

  end

  # Encapsulation of an HTML 5 Websocket.
  #
  # Instances of this class are either created by an {HttpServer} instance when a websocket handshake is accepted
  # on the server, or are create by an {HttpClient} instance when a client succeeds in a websocket handshake with a server.
  # Once an instance has been obtained it can be used to send or receive buffers of data from the connection,
  # a bit like a TCP socket.
  #
  # Instances of this class can only be used from the event loop thread which created it.
  #
  # @author {http://tfox.org Tim Fox}
  class Websocket

    include ReadStream, WriteStream

    # @private
    def initialize(j_ws)
      @j_del = j_ws
    end

    # @return [String] The uri the websocket was created on. When a websocket is first received on the server, the uri can be checked and
    # the websocket can be closed if you want to restrict which uris you wish to accept websockets on.
    def uri
      @j_del.uri
    end

    # Write data to the websocket as a binary frame
    # @param [Buffer] buffer. Data to write.
    def write_binary_frame(buffer)
      @j_del.writeBinaryFrame(buffer._to_java_buffer)
    end

    # Write data to the websocket as a text frame
    # @param [String] str. String to write.
    def write_text_frame(str)
      @j_del.writeTextFrame(str)
    end

    # Close the websocket
    def close
      @j_del.close
    end

  end
end