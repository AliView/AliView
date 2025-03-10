AliView
=======
Download: www.ormbunkar.se/aliview/downloads
<br>Webpage: www.ormbunkar.se/aliview
<br>Help: http://www.ormbunkar.se/aliview/#TOP_HELP

<strong>AliView is yet another alignment viewer and editor, but this one is probably one of the fastest and most intuitive to use, less bloated and hopefully to your liking.</strong>

The general idea when designing this program has always been <strong>usability</strong> and <strong>speed</strong>, all new functions are optimized so they do not affect the general performance and capability to work swiftly with large alignments.

A need to easily sort, view, remove, edit and merge sequences from large transcriptome datasets initiated the work with the program.

A selection of features:
- supports Fasta, Nexus, Phylip, Clustal or MSF-format (unlimited file sizes)
- edit (manually)
- align, add and align automatically (with MUSCLE or MAFFT or any other aligner of your choice)
- find degenerate primers in conserved regions in an alignment of mixed species
- on the fly translation of nucleotides to amino acids
- various visual cues to highlight consensus characters or characters deviating from the consensus
- simple copy/paste/drop/remove of sequences/files
- a very simple to use "external interface" that lets you invoke your other favorite programs (you could for example automatically have the alignment sent to FastTree and then automatically opened in FigTree).

The program is developed at department of Systematic Biology (Uppsala University) so there is probably a predominance in functionality supporting someone working with phylogenies.

Download: www.ormbunkar.se/aliview/downloads

Webpage: www.ormbunkar.se/aliview

[Bioconda package](https://anaconda.org/bioconda/aliview) (unofficial):

``` bash
conda install -c bioconda aliview
```


## Build from source

Tested on 64 bit Xubuntu 17.10 with Oracle java
version 1.8.0_72, and Apache Maven v.3.5.0.

Install Oracle Java 8

     sudo add-apt-repository ppa:webupd8team/java
     sudo apt update
     sudo apt install oracle-java8-installer

Install Apache Maven

     sudo apt install maven

Install makeself

     sudo apt install makeself

Build, package, create installer and install AliView

     git clone https://github.com/AliView/AliView.git
     cd AliView
     mvn clean compile install package | tee mvn.build.log
     ./make_installer_linux.sh
     cd target/linux-version-*
     sudo ./aliview.install.run
