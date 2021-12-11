      program day1part2

C     fortran 77
C      - sliding window
      
      implicit none
      integer x, y, z, t
      integer count
      integer line_no
      character*20 filename
      
      count = 0
      filename = 'second-example.txt'
      if (command_argument_count().eq.1) then
         call get_command_argument(1, filename)
      end if
      open (1, file = filename, status = 'old', err = 404 )
      line_no = 1
      read (1, *, end = 401, err = 402) x
      line_no = 2
      read (1, *, end = 401, err = 402) y
      line_no = 3
      read (1, *, end = 401, err = 402) z      
      
 
      do while(.true.)
         line_no = line_no + 1
         read (1,*, end = 100, err = 402) t
         if ((y + z + t).gt. (x + y + z)) count = count + 1
         x = y
         y = z
         z = t
      end do
      
 100  print "(A,A,A,I4,A)", "in ", filename, " ", count,
     S " measurements are larger than the previous one."
      close (1)
      stop
C
C          Error management
C     
 401  print *, "ERROR: not enough lines in ", filename
      stop
 402  print *, "ERROR: garbled data on line #", line_no
      stop
 404  print "(A,A,A)", "ERROR: File ", filename, " not found"

      end program day1part2
