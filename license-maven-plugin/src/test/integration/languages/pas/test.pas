{
  implements a simple exception type for error code handling
}

unit ErrorCode;

interface
uses SysUtils;

// the exception type
type
  EErrorCode = class(Exception)
  protected
    // member(s)
    m_nErrorCode : Integer;
  public

    // create a new exception with a defined error code
    // -> error code
    constructor Create(nSetErrorCode : Integer); virtual;

    // returns the error code of the exception
    // <- error code
    function GetErrorCode : Integer; virtual;
  end;


implementation


//////////////////////////// EErrorCode ////////////////////////////

constructor EErrorCode.Create(nSetErrorCode : Integer);
begin
  // just store the error code
  m_nErrorCode:=nSetErrorCode;
end;


function EErrorCode.GetErrorCode : Integer;
begin
  Result:=m_nErrorCode;
end;


end.
