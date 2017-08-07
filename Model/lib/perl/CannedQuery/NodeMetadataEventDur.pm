package ClinEpiWebsite::Model::CannedQuery::NodeMetadataEventDur;

@ISA = qw( EbrcWebsiteCommon::Model::CannedQuery );

=pod

=head1 Purpose

This canned query selects various physical characteristics associated with a given participant.

=head1 Macros

The following macros must be available to execute this query.

=over

=item Id - source id for the participant

=back

=cut

# ========================================================================
# ----------------------------- Declarations -----------------------------
# ========================================================================

use strict;

use FileHandle;

use EbrcWebsiteCommon::Model::CannedQuery;

use Data::Dumper;

# ========================================================================
# ----------------------- Create, Init, and Access -----------------------
# ========================================================================

# --------------------------------- init ---------------------------------

sub init {
  my $Self = shift;
  my $Args = ref $_[0] ? shift : {@_};

  $Self->SUPER::init($Args);

  $Self->setName                 ( $Args->{Name        });
  $Self->setId                   ( $Args->{Id          });
  $Self->setEventStart           ( $Args->{EventStart  });
  $Self->setEventDur             ( $Args->{EventDur    });

  my $eventStart = $Self->getEventStart();
  my $eventDur = $Self->getEventDur();

  $Self->setSql(<<Sql);

select pa.name as LEGEND
  , ea.$eventStart as START_DATE
--this next to appease lineplot
  , ea.$eventStart as NAME
  , concat(ea.$eventDur,' day(s)') as DURATION
  , (ea.$eventStart + ea.$eventDur - 1) as END_DATE
-- profile_file is participant id
from apidbtuning.participantattributes pa
   , apidbtuning.protocolappnodeio io
   , APIDBTUNING.EVENTATTRIBUTES ea
where pa.name = \'<<Id>>\'
and pa.protocol_app_node_id = io.input_node_id 
and io.OUTPUT_NODE_ID = ea.PROTOCOL_APP_NODE_ID
and ea.$eventDur is not null
order by $eventStart

Sql

  return $Self;
}


# -------------------------------- access --------------------------------

sub getId                            { $_[0]->{'Id'                         } }
sub setId                            { $_[0]->{'Id'                         } = $_[1]; $_[0] }

sub getName                          { $_[0]->{'Name'                       } }
sub setName                          { $_[0]->{'Name'                       } = $_[1]; $_[0] }

sub getEventStart                    { $_[0]->{'EventStart'                 } }
sub setEventStart                    { $_[0]->{'EventStart'                 } = $_[1]; $_[0] }

sub getEventDur                      { $_[0]->{'EventDur'                   } }
sub setEventDur                      { $_[0]->{'EventDur'                   } = $_[1]; $_[0] }


# ========================================================================
# --------------------------- Support Methods ----------------------------
# ========================================================================

sub prepareDictionary {
  my $Self = shift;
  my $Dict = shift || {};

  $Dict->{Id} = $Self->getId();

  my $Rv = $Dict;

  return $Rv;

}


# ========================================================================
# ---------------------------- End of Package ----------------------------
# ========================================================================

1;
