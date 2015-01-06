The class produce a png bitmap representing the route contained in a csv file.  It expects to find a csv file called route.csv in the launch directory and outputs an image called route.png.  

To use a route and its image, copy route.csv into the model_de directory and copy the route.png into the model_ct directory.





It should be run using the following command

java csvToBitmap <xLeftPos> <xRightPos> <YBottomPos> <yTopPos> <BlackLineWidth> <GreenLineWidth>

The program requires all arguments to be specified

<xLeftPos> is the x coordinate of the left side of the image, in cm.  So if the left hand edge of the image will be at -1.5m you would enter -150.
<xRightPos> is the x coordinate of the right hand side of the image, in cm
<yBottomPos> is the y coordinate of the lower edge of the image, in cm,
<yTopPos> is the top y coordinate of the top edge of the image, in cm.
<BlackLineWidth> specifies the width of the black line in cm
<GreenLineWidth> specifies the width of the Green line in cm

For example:

java csvToBitmap -300 2000 -1150 1150 10 60

will produce a bitmap representing a 23m x 23m square, its left edge is at -3m and the bottom edge is at -11.5m  The black line will be 10cm wide and the green line 60cm wide.  These arguments are compatible with the current 3d model of the tractor, which places the route on a 23 x 23m flat square, with the centre of the square shifted 8.5m in the x direction.  The black line width is intended to hightlight the centre area of the line while the 60cm green line indicates the area 0.3m to either side of the route, i.e. the area within an XTE (cross track error) of 0.3m


This is a rough tool we made to help visualise the line, any questions please ask.(carl.gamble@newcastle.ac.uk)