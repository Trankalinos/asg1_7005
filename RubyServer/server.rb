# Assignment 1 - TCP/IP Socket Programming Server
# Version 1.2
# Author: Martin Javier
# Co-Author: David Tran
# Date: September 22nd, 2013
# Due Date: October 1st, 2013
require 'socket'
include Socket::Constants


# This class will send a binary or a text file over a TCP/IP connection between two hosts.
class Server


  server = TCPServer.new('127.0.0.1', 7005)


  loop do
    Thread.start(server.accept) do |client|
    client.puts "Hello the time is #{Time.now}"

    filename = client.readline

    puts "Reading contents of #{filename}"

    destinationFile = File.open('./text.txt', 'wb')
    destinationFile.print filename

    puts "Done"
    client.close
    end
  end





end
