      program day1part1

C     fortran 77
      
      implicit none
      integer previous, current
      integer count
      integer line_no
      character*20 filename
      
      count = 0
      line_no = 1
      filename = 'small-example.txt'
      if (command_argument_count().eq.1) then
         call get_command_argument(1, filename)
      end if
      open (1, file = filename, status = 'old', err = 404 )
      read (1, *, end = 401, err = 402) current
 
      do while(.true.)
         previous = current
         line_no = line_no + 1
         read (1,*, end = 100, err = 402) current
         if (current .gt. previous) count = count + 1
      end do
      
 100  print "(A,A,A,I4,A)", "in ", filename, " ", count,
     S "measurements are larger than the previous one."
      close (1)
      stop
C
C          Error management
C     
 401  print *, "ERROR: empty file"
      stop
 402  print *, "ERROR: garbled data on line #", line_no
      stop
 404  print "(A,A,A)", "ERROR: File ", filename, " not found"

      end program day1part1
