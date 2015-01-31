<?php

try {
  require_once("Config.php");
}
catch(Exception $e) {
  print $e->getMessage();
}



/*
 *  Conference API.
 */
class Conference extends Service {


  public function __construct()   {


    parent::__construct();
    self::$specificSet=array_merge( parent::$specificSet,self::$specificSet);
    self::$specificGet=array_merge( parent::$specificGet,self::$specificGet);
  }

  
}

?>
