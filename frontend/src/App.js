import React, { Component } from 'react';
import { Grid, Row, Col, PageHeader, Table } from 'react-bootstrap';
import NumberFormat from 'react-number-format';
import 'bootstrap/dist/css/bootstrap.css';
import './App.css';

function Header(props) {
  return (
    <th className="text-center">
      {props.children}
    </th>
  );
}

function TextCell(props) {
  return (
    <td className="text-left">
      {props.text}
    </td>
  );
}

function RightCell(props) {
  return (
    <td className="text-right">
      {props.children}
    </td>
  );
}

function FormattedNumber(props) {
  return (
    <NumberFormat value={props.number}
                  displayType={'text'}
                  thousandSeparator={true} />
  );
}

function NumberCell(props) {
  return (
    <RightCell>
      <FormattedNumber number={props.number} />
    </RightCell>
  );
}

function CrewAndBunks(props) {
  if (props.crew > 0) {
    return (
      <RightCell>
        <FormattedNumber number={props.crew} />
        {' / '}
        <FormattedNumber number={props.bunks} />
      </RightCell>
    );
  } else {
    return (<RightCell></RightCell>);
  }
}

class App extends Component {
  state = {
    isLoading: true,
    data: {}
  }

  componentDidMount() {
    fetch('data.json').then(response => {
      return response.json();
    }).then(data => {
      this.setState({ isLoading: false, data: data });
    });
  }

  renderLabel(text) {
    let style;

    switch (text) {
      case 'human':
      case 'Navy':
      case 'Carrier':
      case 'Cruiser':
      case 'Militia Carrier':
        style = 'human';
        break;
      case 'hai':
      case 'Unfettered Militia':
        style = 'hai';
        break;
      case 'quarg':
        style = 'quarg';
        break;
      case 'korath':
        style = 'korath';
        break;
      case 'wanderer':
      case 'Wanderer':
      case 'Wanderer Military':
        style = 'wanderer';
        break;
      case 'coalition':
        style = 'coalition';
        break;
      default:
    }

    return (<span className={'label label-' + style} key={text}>{text}</span>);
  }

  renderLicenses(ship) {
    return ship.licenses.map(
      license => this.renderLabel(license)
    ).reduce(
      (licenses, license) => (licenses === null ? [license] : [...licenses, ' ', license]),
      null
    );
  }

  renderRows() {
    return this.state.data.map(ship => (
      <tr key={ship.name}>
        <TextCell text={ship.name} />
        <TextCell text={this.renderLabel(ship.race)} />
        <NumberCell number={ship.cost} />
        <TextCell text={ship.category} />
        <NumberCell number={ship.hull} />
        <NumberCell number={ship.shields} />
        <NumberCell number={ship.mass} />
        <NumberCell number={ship.engineCapacity} />
        <NumberCell number={ship.weaponCapacity} />
        <NumberCell number={ship.fuelCapacity} />
        <NumberCell number={ship.outfitSpace} />
        <NumberCell number={ship.cargoSpace} />
        <CrewAndBunks crew={ship.requiredCrew} bunks={ship.bunks} />
        <TextCell text={this.renderLicenses(ship)} />
      </tr>
    ));
  }

  renderTable() {
    return (
      <Table striped bordered condensed hover>
        <thead>
          <tr>
            <Header>Name</Header>
            <Header>Race</Header>
            <Header>Cost</Header>
            <Header>Category</Header>
            <Header>Hull</Header>
            <Header>Shields</Header>
            <Header>Mass</Header>
            <Header>Engine<br />Capacity</Header>
            <Header>Weapon<br />Capacity</Header>
            <Header>Fuel<br />Capacity</Header>
            <Header>Outfit<br />Space</Header>
            <Header>Cargo<br />Space</Header>
            <Header>Crew / bunks</Header>
            <Header>Licenses</Header>
          </tr>
        </thead>
        <tbody>
          {this.renderRows()}
        </tbody>
      </Table>
    );
  }

  render() {
    if (this.state.isLoading) {
      return (<div className="App">Loading...</div>);
    } else {
      return (
        <Grid>
          <Row>
            <Col lg={12}>
              <PageHeader>
                Welcome to Endless Sky encyclopedia!
              </PageHeader>
              {this.renderTable()}
            </Col>
          </Row>
        </Grid>
      );
    }
  }
}

export default App;
