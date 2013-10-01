# Assignment 1 - TCP/IP Socket Programming Server
# Version 1.2
# Author: Martin Javier
# Co-Author: David Tran
# Date: September 22nd, 2013
# Due Date: October 1st, 2013
require 'socket'
include Socket::Constants
SIZE = 1024 * 1024 * 10


# This class will send a binary or a text file over a TCP/IP connection between two hosts.
class Server


  server = TCPServer.new('127.0.0.1', 7005)
  puts "Server On..."

  loop {
    puts "Thread Starting..."
    Thread.start(server.accept) do |client|

      response = client.gets.chomp
      if response == 'SEND'

      filename = gets.chomp
      content = client.read
      file = File.open("./'#{filename}'", 'wb')
      file.print content
      elsif response == 'GET'

      filename = client.gets.chomp

      File.open("./'#{filename}'", 'rb') do |file|
        while chunk = file.read(SIZE)
          client.print(chunk)
          end
        end
      client.puts 'File Sent'
      end





    puts "Done"
    client.close
  end }


    server.close


end
