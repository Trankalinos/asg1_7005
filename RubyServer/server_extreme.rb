# Assignment 1 - TCP/IP Socket Programming Server
# Version 2.2
# Author: Martin Javier
# Co-Author: David Tran
# Date: September 22nd, 2013
# Due Date: October 1st, 2013
require 'socket'
SIZE = 1024 * 1024 * 10


# This class will send a binary or a text file over a TCP/IP connection between two hosts.
class ServerExtreme


  server = TCPServer.new('127.0.0.1', 7005)
  puts 'Server On...'


  # Begins listening to multiple clients on the specified address
  loop {
    puts 'Thread Starting...'
    Thread.start(server.accept) do |client|


      # Request is the variable that is captured from the client
      request = client.gets.chomp

      puts "Client: #{request}"

      # Server is responding to "SEND" request for to be received
      if request == "SEND"
        filename = client.gets.chomp
        puts "Reading contents of #{filename}"
        raw_data = client.read
        # It iterates through the file and saves the data that the client had sent (upload).
        File.open("./#{filename}", 'w+') do |file|
          file.write(raw_data)
          file.close
        end
         client.puts "File Sent"
      # Server is responding to "GET" request for a file to be sent
      elsif request == "GET"
        filename = client.gets.chomp
        puts "Client Requested: #{filename}"
        puts "Sending File..."
        # Reads through the file then delivers the file to the client (download).
        file = File.open(filename, 'r+')    # Read - binary
        contents = file.read
        puts contents
        client.puts(contents)
      end



      # Closing connection with the client
      client.close
    end
  }


  server.close


end
