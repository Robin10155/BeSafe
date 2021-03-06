%STFT
clear all; clc;

filePath = '/home/jayraj/octaveScripts/EE_speech_course/audioFiles/';
type = '*.wav';

files = dir(fullfile(filePath,type));

NFFT = 1024;
display(length(files));

for i = 1:length(files)
	filename = files(i).name;
	[d,fs]=wavread([filePath,filename]);

	Winlen = round(0.02*fs);
	Winshift = round(0.01*fs);
	
	[framed_sig T] = framing(d,Winlen,Winshift);
	T = T/fs;

	win = triang(Winlen);
	w_framed_sig = repmat(win,1,size(framed_sig,2)).*framed_sig;

	STFT = fft(w_framed_sig,NFFT);
	F = 0:fs/NFFT:fs/2;			%%rest is duplicate
	SPECTROGRAM = abs(STFT(1:NFFT/2+1,:)).^2;

	%%%reconstruction
	%this part is incomplete
	%est_d=[];
	%for n=1:size(STFT,2)
	%	est_w_sig = real(ifft(STFT(:,n),NFFT));
	%	est_w_sig = est_w_sig(1:Winlen);	
	%	tmp = 0*d;
	%	tmp((n-1)*Winshift+1:(n-1))--------
	%end

	%following part is to check the overlap add properties of window to get valid
	% ISTFT
	%windows = [];
	%for n=n:Winshift:length(d)-Winlen
	%	t= 0*d;
	%	t(n:n+Winlen-1)=win;
	%	windows = [windows t];
	%endfor

	%figure;
	%plot([1:length(d)]/fs,windows)

	%%following part is to plot the spectrogram
	figure(i);
	surf(T,F,10*log10(abs(SPECTROGRAM)),'EdgeColor','none');
	axis xy; axis tight; colormap(jet); view(0,90);
	title(filename);
	xlabel('Time');
	ylabel('Frequency Hz')
end
