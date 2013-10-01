# Assignment 1 - TCP/IP Socket Programming Client
# Version 1.2
# Author: Martin Javier
# Co-Author: David Tran
# Date: September 22nd, 2013
# Due Date: October 1st, 2013
require 'socket'
require 'benchmark'
include Socket::Constants
SIZE = 1024 * 1024 * 10

# This class will receive a binary or a text file over a TCP/IP connection from the host.
class Client


  puts "Port"
  port = gets.chomp
  puts "Server IP address"
  server_ip = gets.chomp

  puts port, server_ip

  socket = TCPSocket.new(server_ip, port)




  puts "What would you like to do?"
  response = gets.to_s.upcase.chomp
  # Receive the binary or text file from the server.
  if response == 'GET' then


    puts "What is the filename being received?"
    filename =
    data = socket.read
    fileComplete = File.open((File.basename(filename.chomp)), 'wt')
    fileComplete.print IO.read(data)
    fileComplete.close

  # Delivers the binary text file to the server
  elsif response == 'SEND' then


    puts "What is the filename being sent?"
    filename = STDIN.gets.chomp
    socket.puts filename
    file = File.open((File.basename(filename.chomp)), 'rb')    # Read - binary
    fileTransfer = file.read
    socket.puts(fileTransfer)

  end



  puts "The server said, '#{socket.readlines(SIZE)}'"





  socket.close


end