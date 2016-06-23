%%LPCC i think

clear all ; clc
[d,fs] = wavread('shape_16.wav');

Nwin = round(0.02*fs);
Nshift = round(0.02*fs);
p=10;

NFFT=1024;
L=10;			%i think this is for liftering

[framed_sig T] = framing(d,Nwin,Nshift);

for n=1:size(framed_sig,2)
	
	seg = framed_sig(:,n);
	[a,E]= lpc(seg,p);

	seg_fft = fft(seg,NFFT);
	ceps_seg = ifft(log(seg_fft),NFFT);
	ceps_seg(L+1:end-(L-1)) = 0;
	hest = ifft(exp(fft(ceps_seg,NFFT)),NFFT);	

end
