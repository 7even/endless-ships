import React, { Component } from 'react';
import { Grid, Row, Col, PageHeader, Table } from 'react-bootstrap';
import NumberFormat from 'react-number-format';
import 'bootstrap/dist/css/bootstrap.css';
import './App.css';

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
    return ship.licenses.map(license => this.renderLabel(license));
  }

  renderRows() {
    return this.state.data.map(ship => (
      <tr key={ship.name}>
        <td className="text-left">{ship.name}</td>
        <td className="text-left">
          {this.renderLabel(ship.race)}
        </td>
        <td className="text-right">
          <NumberFormat value={ship.cost}
                        displayType={'text'}
                        thousandSeparator={true} />
        </td>
        <td className="text-right">
          <NumberFormat value={ship.hull}
                        displayType={'text'}
                        thousandSeparator={true} />
        </td>
        <td className="text-right">
          <NumberFormat value={ship.shields}
                        displayType={'text'}
                        thousandSeparator={true} />
        </td>
        <td className="text-left">
          {this.renderLicenses(ship)}
        </td>
      </tr>
    ));
  }

  renderTable() {
    return (
      <Table striped bordered condensed hover>
        <thead>
          <tr>
            <th className="text-center">Name</th>
            <th className="text-center">Race</th>
            <th className="text-center">Cost</th>
            <th className="text-center">Hull</th>
            <th className="text-center">Shields</th>
            <th className="text-center">Licenses</th>
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
