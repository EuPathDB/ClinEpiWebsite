package ClinEpiWebsite::View::GraphPackage::Templates::Participant;

use strict;
use vars qw( @ISA );

@ISA = qw( EbrcWebsiteCommon::View::GraphPackage::MixedPlotSet );
use EbrcWebsiteCommon::View::GraphPackage::MixedPlotSet;

use EbrcWebsiteCommon::View::GraphPackage::Util;

use EbrcWebsiteCommon::View::GraphPackage::BarPlot;
use EbrcWebsiteCommon::View::GraphPackage::LinePlot;
use EbrcWebsiteCommon::View::GraphPackage::ScatterPlot;
use EbrcWebsiteCommon::View::GraphPackage::GGScatterPlot;
use EbrcWebsiteCommon::View::GraphPackage::GGLinePlot;
use EbrcWebsiteCommon::View::GraphPackage::GGBarPlot;

use Scalar::Util qw /blessed/;
use Data::Dumper;

#for now will just do something simple, will call it participant summary [prtcpnt_sum]
# may have to do some radjust to manipulate the df(s) into what we want
# will have to add rpostscript to add segments and glyphs for events

#sub noElemFile { return 1; }

sub init {
  my $self = shift;
  $self->SUPER::init(@_);

  my $xAxis = $self->getContXAxis();
  my $yAxis = $self->getYAxis();
  my $eventStart = $self->getEventStart() ? $self->getEventStart() : 'EUPATH_0000644';
  my $eventDur = $self->getEventDur() ? $self->getEventDur() : 'EUPATH_0000665';
  my $test = $self->getTest() ? $self->getTest() : 'EUPATH_0000704';

  my $yLabel = "Weight for Height Z-score";

  if ($yAxis eq 'EUPATH_0000682') {
    $yLabel = "Head Circum for Age Z-score";
  } elsif ($yAxis eq 'EUPATH_0000689') {
    $yLabel = "Height for Age Z-score";
  } elsif ($yAxis eq 'EUPATH_0000733') {
    $yLabel = "Weight for Age Z-score";
  } elsif ($yAxis eq 'EUPATH_0000662') {
    $yLabel = "BMI for Age Z-score";
  } elsif ($yAxis ne 'EUPATH_0000734') {
      warn "This option is not yet recognized. Y-axis label will need to be established in Participant.pm template.";
  }

  my $xLabel = "Age in Days";

  if ($xAxis ne 'EUPATH_0000644') {
    warn "This option is not yet recognized. X-axis label will need to be established in Participant.pm template.";
  }

  my $nodeMetadata =  ({
                        Id => $self->getId(), 
                        contXAxis => $xAxis,  
                        yAxis => $yAxis,
                      });
  my $nodeMetadataEvent = ({ 
                            Id => $self->getId(), 
                            eventStart => $eventStart, 
                            eventDur => $eventDur,
                          });
  my $nodeMetadataTest = ({
                            Id => $self->getId(),
                            contXAxis => $xAxis,
                            test => $test
                          });
#TODO will eventually need for loop here mimic the one in expression.pm to allow plot parts
  my $participantProfile = EbrcWebsiteCommon::View::GraphPackage::Util::makeNodeMetadataSet($nodeMetadata, $nodeMetadataEvent, $nodeMetadataTest);
  my $line = EbrcWebsiteCommon::View::GraphPackage::GGLinePlot::ParticipantSummary->new(@_);
  
  $line->setProfileSets($participantProfile);
  $line->setXaxisLabel($xLabel);
  $line->setYaxisLabel($yLabel);
  $self->setGraphObjects($line);

  return $self;
}

1;
