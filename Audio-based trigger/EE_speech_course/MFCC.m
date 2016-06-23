%STFT
clear all; clc;
[d,fs]=wavread('shape_16.wav');

Winlen = round(0.02*fs);
Winshift = round(0.01*fs);
NFFT = 1024;

[framed_sig T] = framing(d,Winlen,Winshift);
T = T/fs;

win = triang(Winlen);
w_framed_sig = repmat(win,1,size(framed_sig,2)).*framed_sig;

STFT = fft(w_framed_sig,NFFT);
F = 0:fs/NFFT:fs/2;			%%rest is duplicate
SPECTROGRAM = abs(STFT(1:NFFT/2+1,:)).^2;

%%following part is to plot the spectrogram
%surf(T,F,10*log10(abs(SPECTROGRAM)),'EdgeColor','none');
%axis xy; axis tight; colormap(jet); view(0,90);
%xlabel('Time');
%ylabel('Frequency Hz')

Y1 = SPECTROGRAM(1:NFFT/2+1,:);
Z1 = wts*Y1;
Z1 = log(Z1);
Z2 = dct(Z1,13);
