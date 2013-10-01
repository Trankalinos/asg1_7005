# Assignment 1 - TCP/IP Socket Programming Client
# Version 2.2
# Author: Martin Javier
# Co-Author: David Tran
# Date: September 22nd, 2013
# Due Date: October 1st, 2013
require 'socket'

# This class will receive a binary or a text file over a TCP/IP connection from the host.
class ClientExtreme


  puts "Port"
  port = gets.chomp
  puts "Server IP address"
  server_ip = gets.chomp

  puts "Port:#{port}, Server IP:#{server_ip}"

  # Socket is listening to the user specified address
  socket = TCPSocket.new(server_ip, port)

  puts "What would you like to do?"
  response = gets.to_s.upcase.chomp
  # Receive the binary or text file from the server.
  if response == 'GET'
    socket.puts response
    puts "What is the filename being downloaded"
    filename = gets.chomp
    socket.puts filename
    raw_data = socket.read
    puts "Reading contents of #{filename}"
    # It iterates through the content of file to be downloaded
    File.open("./#{filename}", 'w+b') do |file|
      file.write(raw_data)
      file.close
    end
  # Delivers the binary text file to the server
  elsif response == 'SEND'
    socket.puts response
    puts "What is the filename being uploaded?"
    filename = gets.chomp
    socket.puts filename
    # It reads through the content of the file so to be uploaded
    file = File.open((File.basename(filename)), 'r')
    fileContents = file.read
    puts fileContents
    socket.puts(fileContents)
  end



  puts "Goodbye..."





  socket.close


end