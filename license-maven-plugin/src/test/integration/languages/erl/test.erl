%%%-------------------------------------------------------------------
%%% Copyright 2006 Eric Merritt
%%%
%%% Licensed under the Apache License, Version 2.0 (the "License");
%%% you may not use this file except in compliance with the License.
%%% You may obtain a copy of the License at
%%%
%%%       http://www.apache.org/licenses/LICENSE-2.0
%%%
%%%  Unless required by applicable law or agreed to in writing, software
%%%  distributed under the License is distributed on an "AS IS" BASIS,
%%%  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
%%%  implied. See the License for the specific language governing
%%%  permissions and limitations under the License.
%%%
%%%---------------------------------------------------------------------------
%%% @author Eric Merritt <cyberlync@gmail.com>
%%% @doc
%%%   Top level supervisor for the carre app.
%%% @end
%%%----------------------------------------------------------------------------
-module(carre_sup).

-behaviour(supervisor).

%% API
-export([start_link/0]).

%% Supervisor callbacks
-export([init/1]).

-define(SERVER, ?MODULE).

%%====================================================================
%% API functions
%%====================================================================
%%--------------------------------------------------------------------
%% @spec start_link() -> {ok,Pid} | ignore | {error,Error}.
%% @doc Starts the supervisor
%%--------------------------------------------------------------------
start_link() ->
    supervisor:start_link({local, ?SERVER}, ?MODULE, []).

%%====================================================================
%% Supervisor callbacks
%%====================================================================
%%--------------------------------------------------------------------
%% @spec init(Args) -> {ok,  {SupFlags,  [ChildSpec]}} |
%%                     ignore                          |
%%                     {error, Reason}.
%% @doc Whenever a supervisor is started using
%% supervisor:start_link/[2,3], this function is called by the new process
%% to find out about restart strategy, maximum restart frequency and child
%% specifications.
%% @end
%%--------------------------------------------------------------------
init([]) ->
    DynSup = {carre_dyn_sup, {carre_dyn_sup, start_link,[]},
              permanent, 2000, supervisor,[carre_dyn_sup, carre_server]},
    Carre = {carre, {carre, start_link,[]},
              permanent,2000,worker,[carre, carre_server]},
    {ok,{{one_for_one,0,1}, [DynSup, Carre]}}.

%%====================================================================
%% Internal functions
%%====================================================================

