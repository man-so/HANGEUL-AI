document.querySelectorAll('[data-tts]').forEach((btn)=>{btn.addEventListener('click',()=>{if(!('speechSynthesis' in window))return;window.speechSynthesis.cancel();const u=new SpeechSynthesisUtterance(btn.dataset.tts);u.lang='ko-KR';u.rate=.88;window.speechSynthesis.speak(u);});});

// GitHub Pages project sites can be opened with or without a trailing slash.
// Resolve language links against the repository base path so EN does not fall back to /en.html at the domain root.
document.querySelectorAll('.lang-link').forEach((link)=>{
  link.addEventListener('click',(event)=>{
    const href=link.getAttribute('href')||'';
    if(!href.endsWith('index.html')&&!href.endsWith('en.html')) return;
    event.preventDefault();
    const repoBase='/HANGEUL-AI/';
    window.location.href=href.endsWith('en.html')?repoBase+'en.html':repoBase;
  });
});