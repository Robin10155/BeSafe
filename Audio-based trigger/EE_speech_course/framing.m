function [framed_sig T] = framing(sig,windur,winshift)

%%% sig - Nx1 dim array
%%% windur - duaration of the window in number of samples
%%% winshift - window shift in number of samples

framed_sig = [];
T = [];
for n=1:winshift:length(sig)-windur
	framed_sig = [ framed_sig sig(n:n+windur-1)];
	T = [T (n+round(windur/2)-1)];
endfor
